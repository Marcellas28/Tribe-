//package com.dayworks_ltd.loyalty_engine.service;
//
//import com.dayworks_ltd.loyalty_engine.campaign.service.Campaign;
//import com.dayworks_ltd.loyalty_engine.inventory.services.InventoryService;
//import com.dayworks_ltd.loyalty_engine.merchants.Merchant;
//import com.dayworks_ltd.loyalty_engine.merchants.MerchantRepository;
//import com.dayworks_ltd.loyalty_engine.utility.Pair;
//import lombok.RequiredArgsConstructor;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Map;
//import java.util.Optional;
//import java.util.logging.Logger;
//
//@Component
//@RequiredArgsConstructor
//public class DailyCloseScheduler {
//
//    private static final Logger logger = Logger.getLogger(DailyCloseScheduler.class.getName());
//    private final InventoryService inventoryService;
//    private final MerchantRepository merchantRepository; // assuming you have one
//    private final Campaign campaign;
//
//    /**
//     * Runs every day at midnight (00:00)
//     */
//    @Scheduled(cron = "0 0 0 * * *", zone = "Africa/Nairobi")
//    public void autoCloseAllMerchants() {
//        logger.info("=== Auto Close Job Started at " + LocalDateTime.now() + " ===");
//
//        LocalDate d = LocalDate.now(); //revisit this. May result in fetching null summary data,
//                                        // since midnight is the start of new day
//
//        merchantRepository.findAll().forEach(merchant -> {
//            try {
//                Map<String, BigDecimal> summaryData = inventoryService.getDailySummary(String.valueOf(merchant.getId()), d);
//
//                ArrayList<Pair<String, String>> list = new ArrayList<Pair<String, String>>();
//
//                list.add( new Pair<String, String>(
//                        merchant.getBusinessPhone(),
//                        new StringBuilder().append("Gross Sales: ").append(summaryData.get("grossSales"))
//                                .append("  Deductions: ").append(summaryData.get("deductions"))
//                                .append("  Net Sales: ").append(summaryData.get("netSales")).toString()
//                ));
//
//                campaign.sendSMSMessage(list);
//                // ✅ Use ID (or tillNumber if your Inventory uses that)
//                inventoryService.closeDay(String.valueOf(merchant.getId()));
//
//                logger.info("✅ Closed day successfully for merchant ID: " + merchant.getId());
//            } catch (Exception e) {
//                logger.warning("⚠️ Failed to close day for merchant ID: " + merchant.getId()
//                        + " - " + e.getMessage());
//            }
//        });
//
//        logger.info("=== Auto Close Job Completed ===");
//    }
//}
