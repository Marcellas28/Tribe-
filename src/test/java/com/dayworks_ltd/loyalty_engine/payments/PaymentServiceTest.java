package com.dayworks_ltd.loyalty_engine.payments;

import com.dayworks_ltd.loyalty_engine.payments.models.PaymentNotification;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Test
    void recordPayment() {
        Logger.getAnonymousLogger().log( Level.FINE, "hello");
//        PaymentNotification paymentNotification = PaymentNotification.builder()
//                .phoneNumber("0712696965")
//                .firstName("Macharia")
//                .transAmount(10000)
//                .transTime(Date.from(Instant.now()).toString())
//                .transactionType("mpesa")
//
//                .businessShortCode("9878976")
//                .billRefNumber("iuhjkl")
//                .invoiceNumber("123")
//                .orgAccountBalance("210000")
//                .thirdPartyTransID("2")
//
//                .build();
//
//        paymentService.recordPayment( paymentNotification);
//
//
//        paymentNotification = PaymentNotification.builder()
//                .phoneNumber("0712696965")
//                .firstName("Macharia")
//                .transAmount(5000)
//                .transTime(Date.from(Instant.now()).toString())
//                .transactionType("cash")
//
//                .businessShortCode("9878977")
//                .billRefNumber("iuhjklm")
//                .invoiceNumber("1234")
//                .orgAccountBalance("310000")
//                .thirdPartyTransID("21")
//
//                .build();
//
//        paymentService.recordPayment( paymentNotification);
    }
}