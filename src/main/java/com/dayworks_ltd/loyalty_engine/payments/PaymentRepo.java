package com.dayworks_ltd.loyalty_engine.payments;


import com.dayworks_ltd.loyalty_engine.customers.Customer;
import com.dayworks_ltd.loyalty_engine.payments.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long> {

    @Query("SELECT COALESCE(SUM(p.amountPaid), 0) FROM Payment p WHERE p.customer.id = :customerId AND p.transactionTime BETWEEN :start AND :end")
    Integer sumAmountPaidForCustomerBetweenDates(@Param("customerId") Long customerId,
                                                 @Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end);


    @Query("SELECT COUNT(p) FROM Payment p WHERE p.customer.id = :customerId AND p.transactionTime BETWEEN :start AND :end")
    Integer countTransactionsForCustomerBetweenDates(@Param("customerId") Long customerId,
                                                 @Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end);

    Optional<Payment> findByTxnId(String transID);

    //get the latest payment made by a given customer to a given till number
    @NativeQuery(
            value = "SELECT * FROM payment pmt " +
                    "WHERE pmt.customer_id = :customerId " +
                    "AND pmt.till_number = :tillNumber " +
                    "AND pmt.created_at = (SELECT MAX(p.created_at) " +
                                                "FROM payment p " +
                                                "WHERE p.customer_id = :customerId AND p.till_number = :tillNumber " +
                    ")"
    )
    Optional<Payment> getLastPaymentTransactionForCustomerToTillNumber(@Param("customerId") Long customerId,
                                                                    @Param("tillNumber") String tillNumber);
    //get total payments made by a given customer to a given till number
    @NativeQuery(
            value = "SELECT COUNT(p.id) " +
                    "FROM payment p " +
                    "WHERE p.customer_id = :customerId AND p.till_number = :tillNumber"
    )
    int getTotalPaymentTransactionsForCustomerToTillNumber(@Param("customerId") Long customerId,
                                                    @Param("tillNumber") String tillNumber);

    //get average amount paid by a given customer to given till number
    @NativeQuery(
            value = "SELECT AVG(pmt.amount_paid) " +
                    "FROM payment p " +
                    "WHERE p.customer_id = :customerId AND p.till_number = :tillNumber GROUP BY p.customer_id"
    )
    Optional<Double> getAveragePaymentTransactionValueForCustomerToTillNumber(@Param("customerId") Long customerId,
                                                                    @Param("tillNumber") String tillNumber);

    @NativeQuery(
            value = "SELECT c.* FROM customer c WHERE (SELECT COUNT(p.id) " +
                    "FROM payment p " +
                    "WHERE p.customer_id = c.customer_id AND p.till_number = :tillNumber GROUP BY p.customer_id) = 1"
    )
    Optional<List<Customer>> getSinglePaymentCustomers(@Param("tillNumber") String tillNumber);

    @NativeQuery(
                value = "SELECT c.* FROM customer c " +
                        "WHERE ( SELECT COUNT(p.id) FROM payment p WHERE p.customer_id = c.customer_id AND p.till_number = :tillNumber GROUP BY p.customer_id ) >= 5 " //+
                        //"AND () "
    )
    Optional<List<Customer>> getAllLoyalCustomers(@Param("tillNumber") String tillNumber);

    @NativeQuery(
            value = "SELECT c.* FROM customer c " +
                    "WHERE (SELECT SUM(p.amount_paid) FROM payment p WHERE p.customer_id = c.customer_id and p.till_number = :tillNumber GROUP BY p.customer_id) < 5000 "
    )
    Optional<List<Customer>> getLowSpendingCustomers(@Param("tillNumber") String tillNumber);

    @NativeQuery(
            value = "SELECT c.* FROM customer c " +
                    "WHERE (SELECT SUM(p.amount_paid) FROM payment p WHERE p.customer_id = c.customer_id and p.till_number = :tillNumber GROUP BY p.customer_id) BETWEEN 5000 AND 20000 "
    )
    Optional<List<Customer>> getMediumSpendingCustomers(@Param("tillNumber") String tillNumber);

    @NativeQuery(
            value = "SELECT c.* FROM customer c " +
                    "WHERE (SELECT SUM(p.amount_paid) FROM payment p WHERE p.customer_id = c.customer_id and p.till_number = :tillNumber GROUP BY p.customer_id) BETWEEN 20000 AND 50000 "
    )
    Optional<List<Customer>> getHighSpendingCustomers(@Param("tillNumber") String tillNumber);

    @NativeQuery(
            value = "SELECT c.* FROM customer c " +
                    "WHERE (SELECT SUM(p.amount_paid) FROM payment p WHERE p.customer_id = c.customer_id and p.till_number = :tillNumber GROUP BY p.customer_id) >= 50000 "
    )
    Optional<List<Customer>> getVIPCustomers(@Param("tillNumber") String tillNumber);

    @NativeQuery(
            value = "SELECT c.* FROM customer c " +
                    "JOIN payment p ON p.customer_id = c.customer_id " +
                    "WHERE (SELECT COUNT(pmt.id) FROM payment pmt WHERE pmt.customer_id = c.customer_id and pmt.till_number = :tillNumber GROUP BY pmt.customer_id) >= 4 " +
                    "AND (SELECT AVG(pmt.amount_paid) FROM payment pmt WHERE pmt.customer_id = c.customer_id and pmt.till_number = :tillNumber GROUP BY pmt.customer_id) < 5000 "
    )
    Optional<List<Customer>> getFrequentCustomers(@Param("tillNumber") String tillNumber);

    @NativeQuery(
            value = "SELECT c.* FROM customer c " +
                    "JOIN payment p ON p.customer_id = c.customer_id " +
                    "WHERE (SELECT COUNT(pmt.id) FROM payment pmt WHERE pmt.customer_id = c.customer_id and pmt.till_number = :tillNumber GROUP BY pmt.customer_id) <= 2 " +
                    "AND (SELECT AVG(pmt.amount_paid) FROM payment pmt WHERE pmt.customer_id = c.customer_id and pmt.till_number = :tillNumber GROUP BY pmt.customer_id) >= 5000 "
    )
    Optional<List<Customer>> getBulkBuyingCustomers(@Param("tillNumber") String tillNumber);

    @NativeQuery(
            value = "SELECT c.* FROM customer c " +
                    "JOIN payment p ON p.customer_id = c.customer_id " +
                    "WHERE (SELECT COUNT(pmt.id) FROM payment pmt WHERE pmt.customer_id = c.customer_id and pmt.till_number = :tillNumber GROUP BY pmt.customer_id) = 1 "
    )
    Optional<List<Customer>> getOneTimeBuyingCustomers(@Param("tillNumber") String tillNumber);

    @NativeQuery(
            value = "SELECT c.* FROM customer c " +
                    "WHERE c.customer_segment = :dormantSegment " +
                    "AND (SELECT MAX(p.created_at) FROM payment p WHERE p.customer_id = c.customer_id AND p.till_number = :tillNumber GROUP BY c.customer_id) >= CURDATE() - INTERVAL 7 DAY"
    )
    Optional<List<Customer>> getRecoveringCustomers(@Param("tillNumber") String tillNumber, @Param("dormantSegment") String dormantSegment);

    @NativeQuery(
            value = "SELECT c.* FROM customer c " +
                    "WHERE ( SELECT AVG(p.amount_paid) " +
                            "FROM payment p " +
                            "WHERE p.customer_id = c.customer_id " +
                            "AND p.till_number = :tillNumber " +
                            "AND p.created_at < curdate() " +
                            "AND p.created_at >= curdate() - interval 30 day GROUP BY c.customer_id) <= 0.7 * (SELECT AVG(pmt.amount_paid) " + //avarage of current month
                            "FROM payment pmt " +                                                                       //is less than average 70%
                                    "WHERE pmt.customer_id = c.customer_id " +
                                    "AND pmt.till_number = :tillNumber " +                                          //of average of third month
                                    "AND pmt.created_at <= curdate() - interval 60 day " +                              //back
                                    "AND pmt.created_at >= curdate() - interval 90 day GROUP BY c.customer_id)"
    )
    Optional<List<Customer>> getDecliningCustomers(@Param("tillNumber") String tillNumber);

    @NativeQuery(
            value = "SELECT * FROM customer WHERE customer_id = '10'"
    )
    Optional<List<Customer>> getConsistentCustomers(@Param("tillNumber") String tillNumber);
}