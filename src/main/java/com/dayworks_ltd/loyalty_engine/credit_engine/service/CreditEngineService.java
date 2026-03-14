package com.dayworks_ltd.loyalty_engine.credit_engine.service;

import com.dayworks_ltd.loyalty_engine.credit_engine.model.CreditScore;
import com.dayworks_ltd.loyalty_engine.credit_engine.repository.CreditScoreRepository;
import com.dayworks_ltd.loyalty_engine.inventory.models.DailySalesSummary;
import com.dayworks_ltd.loyalty_engine.inventory.models.Inventory;
import com.dayworks_ltd.loyalty_engine.inventory.repositories.DailySalesSummaryRepository;
import com.dayworks_ltd.loyalty_engine.inventory.repositories.InventoryRepository;
import com.dayworks_ltd.loyalty_engine.merchants.Merchant;
import com.dayworks_ltd.loyalty_engine.merchants.MerchantRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CreditEngineService {

    private final MerchantRepository merchantRepo;
    private final DailySalesSummaryRepository salesRepo;
    private final InventoryRepository inventoryRepo;
    private final CreditScoreRepository creditScoreRepo;
    private final ObjectMapper om = new ObjectMapper();

    /**
     * Returns today's credit score for the merchant.
     * - If already computed and stored today → returns it instantly from DB (fast)
     * - If not yet stored today → computes once, saves to DB, returns it
     *
     * Guarantees: exactly one computation per merchant per day.
     */
    public CreditScore getCreditScoreForToday(String merchantId) {
        LocalDate today = LocalDate.now();

        // 1. Try to return already-stored score (most common path)
        Optional<CreditScore> existing = creditScoreRepo
                .findByMerchantIdAndCalculatedForDate(merchantId, today);

        if (existing.isPresent()) {
            log.debug("Returning stored credit score for merchant {} on {}", merchantId, today);
            return existing.get();
        }

        // 2. First time today → compute and store
        log.info("Computing and storing fresh credit score for merchant {} on {}", merchantId, today);
        Result result = calculateScoreInternal(merchantId);

        CreditScore score = CreditScore.builder()
                .merchantId(merchantId)
                .score(result.score)
                .grade(result.grade)
                .calculatedForDate(today)
                .dataFromDate(result.from)
                .dataToDate(result.to)
                .breakdownJson(om.valueToTree(result.breakdown).toString())
                .provisional(result.provisional)
                .build();

        // Race-condition safe: only one thread wins the insert
        try {
            return creditScoreRepo.save(score);
        } catch (DataIntegrityViolationException e) {
            log.info("Race detected: another thread saved score first for merchant {}. Returning stored one.", merchantId);
            return creditScoreRepo
                    .findByMerchantIdAndCalculatedForDate(merchantId, today)
                    .orElseThrow(() -> new IllegalStateException("Score should exist after concurrent save"));
        }
    }

    private record Result(int score, String grade, JsonNode breakdown,
                          LocalDate from, LocalDate to, boolean provisional) {}

    private Result calculateScoreInternal(String merchantId) {
        LocalDate today = LocalDate.now();
        LocalDate from90d = today.minusDays(89);
        LocalDate from30d = today.minusDays(29);

        Merchant merchant = resolveMerchant(merchantId);

        List<DailySalesSummary> sales = salesRepo
                .findByMerchantIdAndRecordDateBetween(merchantId, from90d, today);

        List<Inventory> movements = inventoryRepo
                .findByMerchantIdAndRecordDateBetween(merchantId, from30d, today);

        if (sales.size() < 20) {
            log.warn("Provisional score for merchant {}: only {} sales days found", merchantId, sales.size());
            return provisional("Insufficient data (<20 days)", from90d, today.minusDays(1));
        }

        ObjectNode breakdown = om.createObjectNode();
        int total = 0;

        // 1. Sales Performance – 400 pts
        int consistency = salesConsistency(sales);
        int volume = averageMonthlySales(sales);
        int growth = salesGrowth(sales);
        total += consistency + volume + growth;
        breakdown.set("sales", node(consistency, volume, growth, consistency + volume + growth));

        // 2. Inventory – 350 pts
        int turnover = inventoryTurnover(movements, sales);
        int stockouts = stockOutFrequency(movements);
        int restock = restockDiscipline(movements);
        total += turnover + stockouts + restock;
        breakdown.set("inventory", node(turnover, stockouts, restock, turnover + stockouts + restock));

        // 3. Stability – 150 pts
        int tenure = tenureScore(merchant.getCreatedAt().toLocalDate());
        int volatility = salesVolatility(sales);
        total += tenure + volatility;
        breakdown.set("stability", node(tenure, volatility, 0, tenure + volatility));

        // 4. Identity – 100 pts
        total += 100;
        breakdown.put("identity", 100);

        return new Result(total, grade(total), breakdown, from90d, today.minusDays(1), false);
    }

    private Merchant resolveMerchant(String merchantId) {
        try {
            Long id = Long.parseLong(merchantId);
            return merchantRepo.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Merchant not found: " + merchantId));
        } catch (NumberFormatException e) {
            return merchantRepo.findByTillNumber(merchantId)
                    .orElseThrow(() -> new IllegalArgumentException("Merchant not found by till_number: " + merchantId));
        }
    }

    // ====================== SCORING METHODS ======================

    private int salesConsistency(List<DailySalesSummary> sales) {
        long days = sales.stream()
                .filter(s -> s.getNetSales().compareTo(BigDecimal.valueOf(500)) > 0)
                .count();
        return days >= 25 ? 150 : days >= 20 ? 120 : days >= 15 ? 90 : days >= 10 ? 60 : days >= 5 ? 30 : 10;
    }

    private int averageMonthlySales(List<DailySalesSummary> sales) {
        BigDecimal total = sales.stream()
                .map(DailySalesSummary::getNetSales)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal monthlyAvg = total.divide(BigDecimal.valueOf(3), 2, RoundingMode.HALF_UP);

        if (monthlyAvg.compareTo(new BigDecimal("100000")) >= 0) return 150;
        if (monthlyAvg.compareTo(new BigDecimal("50000")) >= 0) return 120;
        if (monthlyAvg.compareTo(new BigDecimal("25000")) >= 0) return 90;
        if (monthlyAvg.compareTo(new BigDecimal("10000")) >= 0) return 60;
        if (monthlyAvg.compareTo(new BigDecimal("5000")) >= 0) return 30;
        return 10;
    }

    private int salesGrowth(List<DailySalesSummary> sales) {
        LocalDate today = LocalDate.now();
        BigDecimal last30 = sumRange(sales, today.minusDays(59), today.minusDays(30));
        BigDecimal prev30 = sumRange(sales, today.minusDays(89), today.minusDays(60));

        if (prev30.compareTo(BigDecimal.ZERO) == 0) return 40;

        double growth = last30.subtract(prev30)
                .divide(prev30, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();

        if (growth >= 20) return 100;
        if (growth >= 10) return 80;
        if (growth >= 0) return 60;
        if (growth >= -5) return 40;
        if (growth >= -15) return 20;
        return 5;
    }

    private int inventoryTurnover(List<Inventory> moves, List<DailySalesSummary> sales) {
        BigDecimal cogs = sumRange(sales, null, null).multiply(new BigDecimal("0.70"));
        BigDecimal avgStockValue = moves.stream()
                .filter(m -> m.getUnitCost() != null)
                .map(m -> m.getUnitCost().multiply(BigDecimal.valueOf(m.getAvailableStock())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(Math.max(1, moves.size())), 2, RoundingMode.HALF_UP);

        if (avgStockValue.compareTo(BigDecimal.ZERO) <= 0) return 20;

        double turnover = cogs.divide(avgStockValue, 2, RoundingMode.HALF_UP).doubleValue();
        if (turnover >= 8) return 150;
        if (turnover >= 5) return 120;
        if (turnover >= 3) return 90;
        if (turnover >= 1) return 50;
        return 20;
    }

    private int stockOutFrequency(List<Inventory> moves) {
        long days = moves.stream()
                .filter(m -> m.getAvailableStock() == 0)
                .map(Inventory::getRecordDate)
                .distinct()
                .count();

        if (days == 0) return 100;
        if (days <= 3) return 80;
        if (days <= 7) return 60;
        if (days <= 14) return 30;
        return 10;
    }

    private int restockDiscipline(List<Inventory> moves) {
        var dates = moves.stream()
                .filter(m -> m.getAddedStock() != null && m.getAddedStock() > 0)
                .map(Inventory::getRecordDate)
                .sorted()
                .toList();

        if (dates.size() < 3) return 15;

        double avgGap = 0;
        for (int i = 1; i < dates.size(); i++) {
            avgGap += ChronoUnit.DAYS.between(dates.get(i - 1), dates.get(i));
        }
        avgGap /= (dates.size() - 1);

        if (avgGap >= 5 && avgGap <= 10) return 100;
        if (avgGap >= 11 && avgGap <= 20) return 70;
        if (dates.size() >= 4) return 40;
        return 15;
    }

    private int tenureScore(LocalDate registrationDate) {
        long months = ChronoUnit.MONTHS.between(registrationDate, LocalDate.now());
        if (months >= 12) return 80;
        if (months >= 6) return 60;
        if (months >= 3) return 40;
        if (months >= 1) return 20;
        return 5;
    }

    private int salesVolatility(List<DailySalesSummary> sales) {
        var amounts = sales.stream()
                .map(DailySalesSummary::getNetSales)
                .filter(a -> a.compareTo(BigDecimal.ZERO) > 0)
                .map(BigDecimal::doubleValue)
                .toList();

        if (amounts.isEmpty()) return 20;

        double mean = amounts.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double variance = amounts.stream()
                .mapToDouble(a -> Math.pow(a - mean, 2))
                .average().orElse(0);
        double cv = Math.sqrt(variance) / mean;

        if (cv < 0.3) return 70;
        if (cv < 0.6) return 45;
        if (cv < 1.0) return 20;
        return 5;
    }

    private String grade(int s) {
        if (s >= 900) return "A+";
        if (s >= 800) return "A";
        if (s >= 700) return "B";
        if (s >= 600) return "C";
        if (s >= 500) return "D";
        if (s >= 400) return "E";
        return "F";
    }

    private ObjectNode node(int a, int b, int c, int total) {
        return om.createObjectNode()
                .put("a", a)
                .put("b", b)
                .put("c", c)
                .put("total", total);
    }

    private BigDecimal sumRange(List<DailySalesSummary> sales, LocalDate from, LocalDate to) {
        return sales.stream()
                .filter(s -> (from == null || !s.getRecordDate().isBefore(from))
                        && (to == null || s.getRecordDate().isBefore(to)))
                .map(DailySalesSummary::getNetSales)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Result provisional(String reason, LocalDate from, LocalDate to) {
        ObjectNode b = om.createObjectNode()
                .put("provisional", true)
                .put("reason", reason);
        return new Result(300, "E", b, from, to, true);
    }
}