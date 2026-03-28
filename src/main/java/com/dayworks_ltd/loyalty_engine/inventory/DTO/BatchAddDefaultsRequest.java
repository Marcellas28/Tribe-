package com.dayworks_ltd.loyalty_engine.inventory.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchAddDefaultsRequest {
    private String merchantId;                          // ← this is USER ID from frontend
    private List<DefaultProductSelectionDto> selections;
}