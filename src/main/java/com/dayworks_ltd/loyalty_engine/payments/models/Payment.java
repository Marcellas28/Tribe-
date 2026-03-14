package com.dayworks_ltd.loyalty_engine.payments.models;

import com.dayworks_ltd.loyalty_engine.customers.Customer;
import com.dayworks_ltd.loyalty_engine.payments.trigger.PaymentEntityListener;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
@Entity

@Data
@Builder

//@EntityListeners(PaymentEntityListener.class) //listen for inserts on his entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "amount_paid", nullable = false)
    private Integer amountPaid;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status")
    private TransactionStatus transactionStatus;

    @Column(name = "litres_purchased", nullable = false)
    private Double litresPurchased;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(name = "transaction_time", nullable = false)
    private LocalDateTime transactionTime;

    @Column(name = "payer_phone_number", nullable = false)
    private String payerPhoneNumber;

    @Column(name = "mpesa_receipt_number", unique = true)
    private String mpesaReceiptNumber;

    @Column(name = "till_number", nullable = false)
    private String tillNumber;

    @Column(name = "txn_id", nullable = false, unique = true)
    private String txnId;
    @Column(name = "bill_ref_number")
    private String billRefNumber;

    @Column(name = "invoice_number")
    private String invoiceNumber; // NEW: From C2BCallbackDto

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "org_account_balance")
    private String orgAccountBalance;

    @Column(name = "third_party_transaction_id")
    private String thirdPartyTransactionId;

    // NEW: Customer name fields from C2B callback
    @Column(name = "customer_first_name")
    private String customerFirstName;

    @Column(name = "customer_middle_name")
    private String customerMiddleName;

    @Column(name = "customer_last_name")
    private String customerLastName;

    @Column(name = "callback_payload", columnDefinition = "TEXT")
    private String callbackPayload;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (transactionStatus == null) {
            transactionStatus = TransactionStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    public enum TransactionStatus {
        PENDING,
        COMPLETED,
        FAILED,
        CANCELLED,
        REVERSED
    }
}