package com.dayworks_ltd.loyalty_engine.payments;

import com.dayworks_ltd.loyalty_engine.common.ApiResponseBody;
import com.dayworks_ltd.loyalty_engine.customers.Customer;
import com.dayworks_ltd.loyalty_engine.customers.CustomerService;
import com.dayworks_ltd.loyalty_engine.payments.models.*;
import com.dayworks_ltd.loyalty_engine.payments.utils.MpesaUtils;
import com.dayworks_ltd.loyalty_engine.payments.utils.dto.C2BCallbackDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;




import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service

public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    @Autowired
    PaymentRepo paymentRepo;

    @Autowired
    private MpesaUtils mpesaUtil;

    @Autowired
    CustomerService customerService;
    public ApiResponseBody processC2BCallback(C2BCallbackDto callbackDto) {
        ApiResponseBody arb = new ApiResponseBody();

        try {
            log.info("Processing C2B callback for transaction: {}", callbackDto.getTransID());

            // Check if payment already exists (prevent duplicates)
            Optional<Payment> existingPayment = paymentRepo.findByTxnId(callbackDto.getTransID());
            if (existingPayment.isPresent()) {
                log.warn("Payment with transaction ID {} already exists", callbackDto.getTransID());
                arb.setStatus("01");
                arb.setMessage("Payment already recorded");
                arb.setRespObject(existingPayment.get());
                return arb;
            }

            // Build full name from first, middle, last names
            String fullName = buildFullName(callbackDto.getFirstName(), callbackDto.getMiddleName(), callbackDto.getLastName());

            // Find or create customer
            Customer customer = customerService.findOrCreateCustomer(
                    normalizePhoneNumber(callbackDto.getMsisdn()),
                    fullName
            );

            // Map C2B callback to Payment entity
            Payment payment = mapC2bCallbackToPayment(callbackDto, customer);

            // UPDATE CUSTOMER SPENDING STATISTICS
            updateCustomerSpendingStats(customer, Integer.parseInt(callbackDto.getTransAmount()));

            // Save payment
            Payment savedPayment = paymentRepo.save(payment);

            log.info("Successfully recorded C2B payment with ID: {} for transaction: {}",
                    savedPayment.getId(), savedPayment.getTxnId());

            arb.setStatus("00");
            arb.setMessage("C2B payment recorded successfully");
            arb.setRespObject(savedPayment);

        } catch (Exception e) {
            log.error("Error processing C2B callback for transaction: {}", callbackDto.getTransID(), e);
            arb.setStatus("99");
            arb.setMessage("Failed to process C2B payment: " + e.getMessage());
        }

        return arb;
    }


    private void updateCustomerSpendingStats(Customer customer, Integer amount) {
        try {
            customer.updateSpendingStats(amount);
            customerService.saveCustomer(customer); // Make sure you have this method in CustomerService

            log.info("Updated customer {} spending stats: Total Spent={}, Tier={}",
                    customer.getPhoneNumber(), customer.getTotalAmountSpent(), customer.getCustomerTier());
        } catch (Exception e) {
            log.error("Failed to update spending stats for customer: {}", customer.getPhoneNumber(), e);
        }
    }


    private String buildFullName(String firstName, String middleName, String lastName) {
        StringBuilder fullName = new StringBuilder();

        if (firstName != null && !firstName.trim().isEmpty()) {
            fullName.append(firstName.trim());
        }

        if (middleName != null && !middleName.trim().isEmpty()) {
            if (fullName.length() > 0) fullName.append(" ");
            fullName.append(middleName.trim());
        }

        if (lastName != null && !lastName.trim().isEmpty()) {
            if (fullName.length() > 0) fullName.append(" ");
            fullName.append(lastName.trim());
        }

        return fullName.toString();
    }

    private String normalizePhoneNumber(String msisdn) {
        if (msisdn == null) return null;

        if (msisdn.startsWith("254")) {
            return "0" + msisdn.substring(3);
        }
        return msisdn;
    }



    private Payment mapC2bCallbackToPayment(C2BCallbackDto dto, Customer customer) {
        Payment payment = Payment.builder().build();

        // Set customer relationship
        payment.setCustomer(customer);

        // Map basic transaction data
        payment.setAmountPaid(Integer.parseInt(dto.getTransAmount()));
        payment.setTillNumber(dto.getBusinessShortCode());
        payment.setTxnId(dto.getTransID());
        payment.setPayerPhoneNumber(dto.getMsisdn());

        // Map M-Pesa specific fields
        payment.setMpesaReceiptNumber(dto.getTransID());
        payment.setBillRefNumber(dto.getBillRefNumber());
        payment.setInvoiceNumber(dto.getInvoiceNumber());
        payment.setOrgAccountBalance(dto.getOrgAccountBalance());
        payment.setThirdPartyTransactionId(dto.getThirdPartyTransID());

        // Set customer name information
        payment.setCustomerFirstName(dto.getFirstName());
        payment.setCustomerMiddleName(dto.getMiddleName());
        payment.setCustomerLastName(dto.getLastName());

        // Set transaction type and status
        payment.setTransactionType(dto.getTransactionType());
        payment.setTransactionStatus(Payment.TransactionStatus.COMPLETED);

        // Set payment method
        payment.setPaymentMethod("M-PESA");

        // Convert M-Pesa timestamp to LocalDateTime
        payment.setTransactionTime(parseMpesaTimestamp(dto.getTransTime()));

        // Calculate litres (using current fuel price)

        // Store raw callback data for debugging
        payment.setCallbackPayload(dto.toString());

        return payment;
    }


    private LocalDateTime parseMpesaTimestamp(String transTime) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            return LocalDateTime.parse(transTime, formatter);
        } catch (Exception e) {
            log.warn("Failed to parse transaction time: {}, using current time", transTime);
            return LocalDateTime.now();
        }
    }





    public ApiResponseBody recordPayment(PaymentNotification paymentNotification){
        System.out.println("============================= Enter Record payment in payment service=============================");
        ApiResponseBody arb = new ApiResponseBody();

        Payment payment = Payment.builder().build();

        System.out.println("============================= Checking customer in payment service=============================");
        Customer customer = customerService.findOrCreateCustomer(paymentNotification.getPhoneNumber(), paymentNotification.getFirstName());
        System.out.println("============================= After checking customer in payment service=============================");
        payment.setCustomer(customer);
        payment.setPayerPhoneNumber(paymentNotification.getPhoneNumber());
        payment.setTxnId(paymentNotification.getTransID());
        payment.setAmountPaid(paymentNotification.getTransAmount());

        double litresPurchased = paymentNotification.getTransAmount() / 175.0;
        payment.setLitresPurchased(litresPurchased);
        payment.setMpesaReceiptNumber(paymentNotification.getTransID());

        payment.setPaymentMethod(paymentNotification.getTransactionType());
        payment.setTillNumber(paymentNotification.getBusinessShortCode());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime transactionTime = LocalDateTime.parse(paymentNotification.getTransTime(), formatter);
        payment.setTransactionTime(transactionTime);

        System.out.println("============================= Saving payment in payment service=============================");
        paymentRepo.save(payment);
        System.out.println("============================= After saving payment in payment service=============================");
        arb.setStatus("00");
        arb.setMessage("recorded");
        arb.setRespObject(payment);
        System.out.println("============================= Exiting Record payment in payment service=============================");
        return arb;
    }


    public IntiatePaymentResponse intiatePayment(IntiatePaymentRequest intiatePaymentRequest){
        return mpesaUtil.intiatePayment(intiatePaymentRequest);
    }

    public ConfirmPaymentResponse confirmPayment(ConfirmPaymentRequest confirmPaymentRequest){
        return mpesaUtil.confirmPayment(confirmPaymentRequest);
    }
}


