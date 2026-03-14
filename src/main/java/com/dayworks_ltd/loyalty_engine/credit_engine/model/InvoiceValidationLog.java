package com.dayworks_ltd.loyalty_engine.credit_engine.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoice_validation_logs",
        indexes = {
                @Index(name = "idx_submission_id", columnList = "submissionId"),
                @Index(name = "idx_merchant_validation", columnList = "merchantId,createdAt")
        }
)
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class InvoiceValidationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long merchantId;

    @Column(length = 50)
    private String submissionId;  // "CTZETFPV"

    @Column(nullable = false, length = 20)
    private String status;  // SUCCESS, REJECTED, ERROR

    @Column(length = 100)
    private String invoiceNumber;

    @Column(columnDefinition = "TEXT")
    private String validationErrors;  // JSON array of errors

    @Column(precision = 4, scale = 2)
    private BigDecimal ocrConfidence;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}