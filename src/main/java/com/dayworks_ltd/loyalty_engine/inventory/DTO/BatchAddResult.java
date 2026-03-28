package com.dayworks_ltd.loyalty_engine.inventory.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BatchAddResult {
    private int addedCount;
    private int skippedCount;
}