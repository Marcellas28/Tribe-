package com.dayworks_ltd.loyalty_engine.credit_engine.model;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "supplier_invoice_items",
        indexes = {
                @Index(name = "idx_invoice_id", columnList = "invoiceId"),
                @Index(name = "idx_merchant_item", columnList = "merchantId,normalizedName"),
                @Index(name = "idx_normalized_name", columnList = "normalizedName")
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierInvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long invoiceId;  // FK to supplier_invoices (we'll add that table next)

    @Column(nullable = false)
    private String merchantId;  // Denormalized for faster queries

    @Column(nullable = false, length = 255)
    private String rawName;  // "Unga Maize Flour 2kg" (from OCR, untouched)

    @Column(nullable = false, length = 255)
    private String normalizedName;  // "Maize Flour 2kg" (cleaned by AI)

    @Column(nullable = false)
    private Integer quantity;  // 20

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitCost;  // 210.00

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal lineTotal;  // 4200.00

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}