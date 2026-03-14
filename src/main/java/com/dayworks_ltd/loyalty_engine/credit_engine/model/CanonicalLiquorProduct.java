package com.dayworks_ltd.loyalty_engine.credit_engine.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "canonical_liquor_products",
        indexes = {
                @Index(name = "idx_canonical_name", columnList = "canonicalName", unique = true),
                @Index(name = "idx_brand_variant_volume", columnList = "brand, variant, volumeMl"),
                @Index(name = "idx_category", columnList = "category"),
                @Index(name = "idx_product_code", columnList = "productCode", unique = true)
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CanonicalLiquorProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, unique = true)
    private String productCode;

    @Column(length = 100, nullable = false)
    private String brand;

    @Column(length = 100)
    private String productLine;

    @Column(length = 80, nullable = false)
    private String variant;

    @Column(length = 50, nullable = false)
    private String category;

    @Column(nullable = false)
    private Integer volumeMl;

    @Column(length = 30)
    private String packaging;

    @Column(precision = 4, scale = 2)
    private BigDecimal alcoholPercentage;

    // Auto-generated canonical name – used for matching & uniqueness
    @Column(nullable = false, unique = true, length = 255)
    private String canonicalName;           // e.g. "Jack Daniels Tennessee Whiskey Honey 750ml Bottle"

    // Suggested/default selling price from distributor pricelist
    @Column(precision = 12, scale = 2)
    private BigDecimal suggestedRetailPrice;

    // Optional: typical buying cost (for margin calculation)
    @Column(precision = 12, scale = 2)
    private BigDecimal suggestedCostPrice;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        generateCanonicalName();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        generateCanonicalName();
    }

    /**
     * Automatically generate a clean, consistent name for matching
     */
    private void generateCanonicalName() {
        if (brand == null || variant == null || volumeMl == null) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(brand.trim());

        if (productLine != null && !productLine.trim().isEmpty()) {
            sb.append(" ").append(productLine.trim());
        }

        if (!variant.trim().isEmpty() && !variant.equalsIgnoreCase("ORIGINAL")) {
            sb.append(" ").append(variant.trim());
        }

        sb.append(" ").append(volumeMl).append("ml");

        if (packaging != null && !packaging.trim().isEmpty()) {
            sb.append(" ").append(packaging.trim());
        }

        this.canonicalName = sb.toString()
                .replaceAll("\\s+", " ")
                .trim();
    }
}