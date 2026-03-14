package com.dayworks_ltd.loyalty_engine.payments.utils.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConfirmPaymentDto {

    @SerializedName("BusinessShortCode")
    private String businessShortCode;

    @SerializedName("Password")
    private String password;

    @SerializedName("Timestamp")
    private String timestamp;

    @SerializedName("CheckoutRequestID")
    private  String checkoutRequestID;
}
