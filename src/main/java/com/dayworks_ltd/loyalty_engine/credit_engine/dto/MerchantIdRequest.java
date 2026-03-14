package com.dayworks_ltd.loyalty_engine.credit_engine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MerchantIdRequest {
    private String merchant_id;

    public String getMerchantId() {
        return merchant_id;
    }
}