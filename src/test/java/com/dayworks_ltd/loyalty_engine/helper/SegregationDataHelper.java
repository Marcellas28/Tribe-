package com.dayworks_ltd.loyalty_engine.helper;

import com.dayworks_ltd.loyalty_engine.common.customersType;
import com.dayworks_ltd.loyalty_engine.customers.Customer;
import com.dayworks_ltd.loyalty_engine.customers.CustomerRepo;
import com.dayworks_ltd.loyalty_engine.dto.CustomerDto;
import com.dayworks_ltd.loyalty_engine.payments.PaymentRepo;
import com.dayworks_ltd.loyalty_engine.payments.models.Payment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

/**
 * This is a test for inserting data to the customer and payment entities.
 * The data can then be used for testing the segregation service
 *
 * This is intended for development use only.
 *
 * To run the test, open the Payment.java file and comment out the EntityEventListener annotation.
 * Also comment out the onCreate() and onUpdate() methods. They override the generated test date values.
 * **/

@SpringBootTest
public class SegregationDataHelper {

    @Autowired
    private CustomerRepo customerRepository;

    @Autowired
    private PaymentRepo paymentRepository;

    @Test
    public void createCustomers()
    {
        int day = 12, month = 10;
        int lastPaymentDay = 12, lastPaymentMonth = 9;
        int phone = 711000000;
        for( int i = 0; i <= 60; i++)
        {
            if( day == 0 )
            {
                day = 30;
                month = 9;
            }

            if( lastPaymentDay <= 0 && lastPaymentMonth == 9 )
            {
                lastPaymentDay = 31;
                lastPaymentMonth = 8;
            }
            else if( lastPaymentDay <= 0 && lastPaymentMonth == 8 )
            {
                lastPaymentDay = 31;
                lastPaymentMonth = 7;
            }
            else if( lastPaymentDay <= 0 && lastPaymentMonth == 7 )
            {
                lastPaymentDay = 30;
                lastPaymentMonth = 6;
            }
            else if( lastPaymentDay <= 0 && lastPaymentMonth == 6 )
            {
                lastPaymentDay = 31;
                lastPaymentMonth = 5;
            }
            else if( lastPaymentDay <= 0 && lastPaymentMonth == 5 )
            {
                lastPaymentDay = 30;
                lastPaymentMonth = 4;
            }

            Customer customer = Customer.builder()
                    .phoneNumber("0" + phone)
                    .customerType(customersType.PERSONAL)
                    .customerSegment("Loyal")
                    .locality("Kisumu")
                    .isLead(false)
                    .name("Customer " + i)
                    .lastTransaction(LocalDateTime.of(2025, (i <= 30) ? 10 : lastPaymentMonth, (i <= 30) ? 14 : lastPaymentDay, 15, 58))
                    .createdAt(LocalDateTime.of(2025, month, day,12,43))
                    .totalLitres(123.0)
                    .totalTransactions(12)
                    .totalAmountSpent(15000)
                    .gender("Female")
                    .customerTier(Customer.CustomerTier.GOLD)
                    .lastPaymentDate(LocalDateTime.of(2025, (i <= 30) ? 10 : lastPaymentMonth, (i <= 30) ? 14 : lastPaymentDay, 15, 58))
                    .averageTransactionValue(15000.0)
                    .collectedBy(null)
                    .build();

            customerRepository.save(customer);
            day--;
            phone++;

            if(i > 30)
            {
                lastPaymentDay -= 3;
            }
        }
    }

    @Test
    public void createPayment()
    {
//        List<Customer> customers = customerRepository.findAll();
//
//        int[] daysPerMonth = {31,28,31,30,31,30,31,31,30,31,30,31};
//
//        int randomMonth, randomDay, randomAmount;
//
//        int txnId = 110000, mpesaRecNum = 220000;
//
//        for( int i = 1; i <= customers.size(); i++ ) {
//            int count = (i % 5) + 1;
//
//            for(int j = 0; j < count; j++)
//            {
//                randomMonth = 5 + (int)(Math.random() * 6); //month to be between May and
//                randomDay = 1 + (int)(Math.random() * daysPerMonth[randomMonth - 1]);
//                randomAmount = 4000 + (int)(Math.random() * 51000);
//
//                Payment payment = Payment.builder()
//                        .billRefNumber("23423423")
//                        .callbackPayload("payload_here")
//                        .customerFirstName("Customer")
//                        .customerMiddleName("MName")
//                        .customerLastName("LName")
//                        .paymentMethod("MPESA")
//                        .invoiceNumber("AWSDASmDFAS")
//                        .txnId("AFDSASDFlAS" + txnId)                       //must be unique
//                        .mpesaReceiptNumber("SWDASFDmWADFSA" + mpesaRecNum) //must be unique
//                        .litresPurchased(123.4)
//                        .payerPhoneNumber("0711243345")
//                        .thirdPartyTransactionId("ASDFSDF")
//                        .orgAccountBalance("123456789.99")
//                        .transactionStatus(Payment.TransactionStatus.COMPLETED)
//                        .transactionTime(LocalDateTime.of(2025, randomMonth, randomDay, 15, 15))
//                        .transactionType("PAYMENT")
//                        .updatedAt(LocalDateTime.of(2025, randomMonth, randomDay, 15, 15))
//                        .tillNumber("12345")
//                        .amountPaid(randomAmount)
//                        .createdAt(LocalDateTime.of(2025, randomMonth, randomDay, 15, 15))
//                        .customer(customers.get(i - 1))
//                        .build();
//
//                paymentRepository.save(payment);
//
//                txnId++;
//                mpesaRecNum++;
//            }
//        }
    }
}
