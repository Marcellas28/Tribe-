package com.dayworks_ltd.loyalty_engine.customers;

import com.dayworks_ltd.loyalty_engine.dto.CustomerDto;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepo extends JpaRepository<Customer, Long> {
    Optional<Customer> findByPhoneNumber(String phoneNumber);


    List<Customer> findAllByIsLeadIsTrueAndCreatedAtBetween(
            LocalDateTime start, LocalDateTime end);

    @NativeQuery(
            value = "SELECT c.customer_id as customerId, c.phone_number as phoneNumber, " +
                    "c.name as name, c.total_litres as totalLitres, c.last_transaction as lastTransaction, " +
                    "c.created_at as createdAt, lp.points  as loyaltyPoints " +
                    "FROM customer c " +
                    "JOIN loyalty_points lp on c.customer_id = lp.customer_id " +
                    "WHERE  last_transaction < :date"
    )
    List<CustomerDto> getAllLostCustomers(@Param("date") String fiveDaysEarlier);

    @NativeQuery(
            value = "SELECT c.customer_id as customerId, c.phone_number as phoneNumber, " +
                    "c.name as name, c.total_litres as totalLitres, c.last_transaction as lastTransaction, " +
                    "c.created_at as createdAt, lp.points  as loyaltyPoints " +
                    "FROM customer c " +
                    "JOIN loyalty_points lp on c.customer_id = lp.customer_id "
    )
    List<CustomerDto> getAllCustomers();

    /**
     * UPDATE CUSTOMER SEGMENT
     * **/
    @Transactional
    @Modifying
    @NativeQuery(
            value = "UPDATE customer c " +
                    "SET c.customer_segment = :newCustomerSegment " +
                    "WHERE c.customer_id = :customerId"
    )
    int updateCustomerSegment(@Param("customerId") Long customerId,
                                             @Param("newCustomerSegment") String newCustomerSegment);

    /**
     * FETCH CUSTOMERS THAT BELONG TO SPECIFIED SEGMENT
     * **/
    @NativeQuery(
            value = "SELECT * FROM customer c WHERE c.customer_segment = :customerSegement"
    )
    Optional<List<Customer>> getCustomersInSegment(@Param("customerSegment") String customerSegment);


    @NativeQuery(
            value = "SELECT c.* FROM customer c " +
                    "WHERE c.created_at >= (CURDATE() - INTERVAL 7 DAY) " +
                    "AND EXISTS ( SELECT * FROM payment p WHERE p.customer_id = c.customer_id AND p.till_number = :tillNumber)"
    )
    Optional<List<Customer>> getAllCustomersCreatedWithinLastSevenDays(@Param("tillNumber") String tillNumber);

    @NativeQuery(
            value = "SELECT c.* FROM customer c " +
                    "WHERE c.created_at >= (CURDATE() - INTERVAL 14 DAY) " +
                    "AND (SELECT COUNT(id) FROM payment p WHERE p.customer_id = c.customer_id AND p.till_number = :tillNumber GROUP BY p.customer_id) > 1" //must have made more than one payment
    )
    Optional<List<Customer>> getAllCustomersCreatedWithinLastFourteenDaysAndHavePaidMoreThanTwice(@Param("tillNumber") String tillNumber);

    @NativeQuery(
            value = "SELECT c.* FROM customer c " +
                    "JOIN (SELECT p.customer_id as customer_id, MAX(p.created_at) as created_at FROM payment p WHERE p.till_number = :tillNumber GROUP BY p.customer_id) as pmt " +
                    "ON pmt.customer_id = c.customer_id " +
                    "WHERE pmt.created_at <= CURDATE() - INTERVAL 30 DAY AND pmt.created_at >= CURDATE() - INTERVAL 90 DAY "
    )
    Optional<List<Customer>> getDormantCustomers(@Param("tillNumber") String tillNumber);

    @NativeQuery(
            value = "SELECT c.* FROM customer c " +
                    "JOIN (SELECT p.customer_id as customer_id, MAX(p.created_at) as created_at FROM payment p WHERE p.till_number = :tillNumber GROUP BY p.customer_id) as pmt " +
                    "ON pmt.customer_id = c.customer_id " +
                    "WHERE pmt.created_at < (CURDATE() - INTERVAL 90 DAY)"
    )
    Optional<List<Customer>> getChurnedCustomers(@Param("tillNumber") String tillNumber);
}
