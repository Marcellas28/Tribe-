package com.dayworks_ltd.loyalty_engine.inventory.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "daily_sales_summary",
        uniqueConstraints = @UniqueConstraint(columnNames = {"merchant_id", "record_date"})
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailySalesSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "merchant_id", nullable = false)
    private String merchantId;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal grossSales = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal deductions = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal netSales = BigDecimal.ZERO;

    @Column(name = "is_closed", nullable = true, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isClosed = false;  // ADD THIS FIELD




    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
