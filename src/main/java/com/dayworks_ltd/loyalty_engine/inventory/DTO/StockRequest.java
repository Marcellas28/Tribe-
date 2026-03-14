package com.dayworks_ltd.loyalty_engine.inventory.DTO;

import lombok.Data;

import java.util.List;

@Data
public class StockRequest {
    private String merchantId;
    private List<StockItemRequest> items;
}