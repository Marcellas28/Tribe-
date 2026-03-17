package com.dayworks_ltd.loyalty_engine.inventory.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DefaultProductDto {
    private String productName;
    private String productCode;
    private Integer volumeMl;
}
