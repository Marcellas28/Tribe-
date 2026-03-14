package com.dayworks_ltd.loyalty_engine.service;

import com.dayworks_ltd.loyalty_engine.Repository.LoyaltyPointsRepository;
import com.dayworks_ltd.loyalty_engine.customers.Customer;
import com.dayworks_ltd.loyalty_engine.payments.PaymentRepo;
import com.dayworks_ltd.loyalty_engine.payments.models.LoyaltyPoints;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.dayworks_ltd.loyalty_engine.customers.CustomerRepo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
public class LoyaltyService {

    @Autowired
    private PaymentRepo paymentRepository;

    @Autowired
    private LoyaltyPointsRepository loyaltyPointsRepository;

    @Autowired
    private CustomerRepo customerRepository;

    /**
     * Awards loyalty points to a customer based on their monthly expenditure.
     */
    @Transactional
    public void calculateAndAwardMonthlyPoints(Customer customer, YearMonth month) {
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();

        Integer totalSpend = paymentRepository.sumAmountPaidForCustomerBetweenDates(
                customer.getCustomerId(), start.atStartOfDay(), end.atTime(23, 59, 59)
        );

        if (totalSpend == null || totalSpend < 3000) return; // Minimum threshold

        // Base points: 1 point per 100 Ksh
        int points = totalSpend / 100;

        // Bonus tiers for heavy spenders
        if (totalSpend >= 15000) points += 50;
        else if (totalSpend >= 10000) points += 30;
        else if (totalSpend >= 8000) points += 20;
        else if (totalSpend >= 5000) points += 10;
        else if (totalSpend >= 3000) points += 5;

        // Create LoyaltyPoints record
        LoyaltyPoints lp = new LoyaltyPoints();
        lp.setCustomer(customer);
        lp.setMonth(month);
        lp.setPoints(points);
        lp.setAwardedAt(LocalDateTime.now());
        lp.setRedeemed(false);

        loyaltyPointsRepository.save(lp);
    }

    /**
     * Allows a customer to redeem loyalty points only between the 30th and 3rd of the next month.
     */

    @Transactional
    public boolean redeemPoints(Customer customer, int pointsToRedeem) {
        int day = LocalDate.now().getDayOfMonth();

        // Redemption window: only between 30th and 3rd (inclusive)
        if (!(day >= 30 || day <= 3)) return false;

        // 🔧 FIX: Include customer as the first parameter
        List<LoyaltyPoints> unredeemed = loyaltyPointsRepository
                .findByCustomerAndRedeemedFalseAndMonthBefore(customer, YearMonth.now());

        int totalAvailable = unredeemed.stream().mapToInt(LoyaltyPoints::getPoints).sum();
        if (pointsToRedeem > totalAvailable) return false;

        int remaining = pointsToRedeem;
        for (LoyaltyPoints lp : unredeemed) {
            if (remaining <= 0) break;

            int deduct = Math.min(lp.getPoints(), remaining);
            lp.setPoints(lp.getPoints() - deduct);

            if (lp.getPoints() == 0) {
                lp.setRedeemed(true);
            }

            loyaltyPointsRepository.save(lp);
            remaining -= deduct;
        }

        return true;
    }

    /**
     * Automatically expires unredeemed points on the 4th of every month.
     */
    @Scheduled(cron = "0 0 0 4 * *")
    @Transactional
    public void expireOldPoints() {
        YearMonth lastMonth = YearMonth.now().minusMonths(1);

        List<Customer> customers = customerRepository.findAll(); // Assuming you have this

        for (Customer customer : customers) {
            List<LoyaltyPoints> expired = loyaltyPointsRepository
                    .findByCustomerAndRedeemedFalseAndMonthBefore(customer, lastMonth);

            for (LoyaltyPoints lp : expired) {
                lp.setRedeemed(true);
            }


            loyaltyPointsRepository.saveAll(expired); // ✅ More efficient than individual saves

        }
    }
}
