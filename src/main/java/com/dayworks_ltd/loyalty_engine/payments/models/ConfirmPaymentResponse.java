package com.dayworks_ltd.loyalty_engine.payments.models;

import lombok.Data;

@Data
public class ConfirmPaymentResponse {
    private String status;
    private String message;
}
