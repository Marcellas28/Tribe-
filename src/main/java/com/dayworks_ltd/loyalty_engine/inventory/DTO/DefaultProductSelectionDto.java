package com.dayworks_ltd.loyalty_engine.inventory.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DefaultProductSelectionDto {
    private String productName;
    private String productCode;
    private Integer volumeMl;
    private Integer startingStock;   // can be null
    private BigDecimal unitPrice;
}