package com.dayworks_ltd.loyalty_engine.payments.utils.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class InitPaymentRespDto {

    @SerializedName("MerchantRequestID")
    private String merchantRequestId;

    @SerializedName("CheckoutRequestID")
    private String checkoutRequestID;

    @SerializedName("ResponseCode")
    private String responseCode;

    @SerializedName("ResponseDescription")
    private String responseDescription;

    @SerializedName("CustomerMessage")
    private String customerMessage;
}
