package com.dayworks_ltd.loyalty_engine.payments.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class IntiatePaymentRequest {
    @JsonProperty("PhoneNumber")
    private String phoneNumber;

    @JsonProperty("Amount")
    private String amount;

    @JsonProperty("Username")
    private String username;
}