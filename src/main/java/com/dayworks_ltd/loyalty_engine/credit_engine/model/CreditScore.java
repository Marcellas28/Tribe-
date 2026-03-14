package com.dayworks_ltd.loyalty_engine.credit_engine.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "credit_scores", uniqueConstraints = @UniqueConstraint(columnNames = {"merchant_id", "calculated_for_date"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "merchant_id", nullable = false, length = 100)
    private String merchantId;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false, length = 5)
    private String grade;

    @Column(name = "calculated_for_date", nullable = false)
    private LocalDate calculatedForDate;

    @Column(name = "data_from_date", nullable = false)
    private LocalDate dataFromDate;

    @Column(name = "data_to_date", nullable = false)
    private LocalDate dataToDate;

    @Column(columnDefinition = "JSON")
    private String breakdownJson;

    @Column(name = "is_provisional", nullable = false)
    private boolean provisional = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}