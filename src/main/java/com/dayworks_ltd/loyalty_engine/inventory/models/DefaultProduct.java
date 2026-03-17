package com.dayworks_ltd.loyalty_engine.inventory.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_defaults")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefaultProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @Column(name = "product_code", nullable = false, unique = true, length = 36)
    private String productCode;

    @Column(name = "volume_ml", nullable = false)
    private Integer volume_Ml;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        if (productCode == null || productCode.isBlank()) {
            productCode = java.util.UUID.randomUUID().toString();
        }
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
