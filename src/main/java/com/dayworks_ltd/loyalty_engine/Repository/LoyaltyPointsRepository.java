package com.dayworks_ltd.loyalty_engine.Repository;

import com.dayworks_ltd.loyalty_engine.customers.Customer;
import com.dayworks_ltd.loyalty_engine.dto.CustomerDto;
import com.dayworks_ltd.loyalty_engine.payments.models.LoyaltyPoints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;

import java.time.YearMonth;
import java.util.List;

public interface LoyaltyPointsRepository extends JpaRepository<LoyaltyPoints, Long> {
    List<LoyaltyPoints> findByCustomerAndMonth(Customer customer, YearMonth month);
    List<LoyaltyPoints> findByCustomerAndRedeemedFalseAndMonthBefore(Customer customer, YearMonth cutoffMonth);

    @NativeQuery(
            value = "SELECT c.customer_id, c.phone_number, c.name, c.total_litres, " +
                    "c.last_transaction, c.created_at" +
                    " FROM customer c " +
                    "JOIN loyalty_points lp ON lp.customer_id = c.customer_id " +
                    "WHERE lp.points >= 200"
    )
    List<Customer> getAllCustomersWithSufficientLoyaltyPoints();

    @NativeQuery(
            value = "SELECT c.customer_id as customerId, c.phone_number as phoneNumber, " +
                    "c.name as name, c.total_litres as totalLitres, " +
                    "c.last_transaction as lastTransaction, c.created_at as createdAt, lp.points  as loyaltyPoints " +
                    "FROM customer c " +
                    "JOIN loyalty_points lp ON lp.customer_id = c.customer_id "
    )
    List<CustomerDto> getAllCustomersWithLoyaltyPoints();
}
