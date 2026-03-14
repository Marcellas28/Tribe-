package com.dayworks_ltd.loyalty_engine.payments.models;

import lombok.Data;

@Data
public class ConfirmPaymentRequest {
    private String checkoutRequestID;
}
