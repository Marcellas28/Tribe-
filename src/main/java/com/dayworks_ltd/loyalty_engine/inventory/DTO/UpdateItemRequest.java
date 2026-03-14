package com.dayworks_ltd.loyalty_engine.inventory.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateItemRequest {
    private String merchantId;        // Required for security

    private String itemName;          // Optional
    private Integer quantity;         // Optional (updates availableStock)
    private BigDecimal unitPrice;     // Optional


}