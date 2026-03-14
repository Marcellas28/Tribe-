package com.dayworks_ltd.loyalty_engine.inventory.DTO;


import lombok.Data;

@Data
public class StockItemRequest {
    private Long inventoryId;
    private int quantity;
}