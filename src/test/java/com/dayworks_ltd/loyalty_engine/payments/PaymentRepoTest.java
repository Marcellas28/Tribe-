package com.dayworks_ltd.loyalty_engine.payments;

import com.dayworks_ltd.loyalty_engine.payments.models.Payment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentRepoTest {

    @Autowired
    private PaymentRepo paymentRepo;

    @Test
    void getLastPaymentTransactionForCustomerToTillNumber() {
        Long customerId = 1L;
        String tillNumber = "1234";
        Optional<Payment> payment = paymentRepo.getLastPaymentTransactionForCustomerToTillNumber(
                customerId, tillNumber);

        if( payment.isPresent() )
        {
            System.out.println("======================================");
            System.out.println("Ref: " + payment.get().getBillRefNumber()
                    + "  Till: " + payment.get().getTillNumber() + "  Amount: " + payment.get().getAmountPaid());
            System.out.println("======================================");
        }
        else
        {
            System.out.println("======================================");
            System.out.println("Could not get last payment for the customer: " + customerId
                            +  " to the till: " + tillNumber);
            System.out.println("======================================");
        }
    }

    @Test
    void getTotalPaymentTransactionsForCustomerToTillNumber() {
        int totalPayments = paymentRepo.getTotalPaymentTransactionsForCustomerToTillNumber(1L, "1234");

        System.out.println("======================================");
        System.out.println("Total Payments: " + totalPayments);
        System.out.println("======================================");
    }

    @Test
    void getAveragePaymentTransactionValueForCustomerToTillNumber() {
        Long customerId = 1L;
        String tillNumber = "1234";
        Optional<Double> averagePayment = paymentRepo.getAveragePaymentTransactionValueForCustomerToTillNumber(
                customerId, tillNumber);

        if(averagePayment.isPresent() )
        {
            System.out.println("======================================");
            System.out.println("Average Payment: " + averagePayment.get());
            System.out.println("======================================");
        }
        else{
            System.out.println("======================================");
            System.out.println("Could not get average payment for customer: " + customerId
                    + " to till: " + tillNumber);
            System.out.println("======================================");
        }
    }
}