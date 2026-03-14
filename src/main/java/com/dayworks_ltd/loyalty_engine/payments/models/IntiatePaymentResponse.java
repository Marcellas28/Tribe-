package com.dayworks_ltd.loyalty_engine.payments.models;

import lombok.Data;

@Data
public class IntiatePaymentResponse {
    private String status;
    private String message;
    private String checkoutRequestID;
}
