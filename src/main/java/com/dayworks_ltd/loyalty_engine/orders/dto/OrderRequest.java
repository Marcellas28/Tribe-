package com.dayworks_ltd.loyalty_engine.orders.dto;


import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderRequest {
    private Long distributorId;
    private List<OrderItemRequest> items;

    // Payment Info
    private String phoneNumber;      // Must be in format 2547xxxxxxxx
}