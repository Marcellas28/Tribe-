package com.dayworks_ltd.loyalty_engine.credit_engine.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "suppliers",
        indexes = {
                @Index(name = "idx_supplier_normalized_name", columnList = "normalizedName"),
                @Index(name = "idx_supplier_pin", columnList = "pin")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uc_supplier_normalized", columnNames = {"normalizedName", "pin"})
        }
)
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 255)
    private String normalizedName;

    @Column(length = 50)
    private String pin;

    @Column(length = 15)
    private String phone;

    @Column(length = 255)
    private String email;

    @Column(length = 500)
    private String address;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        // Auto-normalize name
        if (normalizedName == null && name != null) {
            normalizedName = name.toLowerCase()
                    .replaceAll("[^a-z0-9\\s]", "")
                    .replaceAll("\\s+", " ")
                    .trim();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}