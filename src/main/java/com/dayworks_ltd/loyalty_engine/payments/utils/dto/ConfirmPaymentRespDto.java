package com.dayworks_ltd.loyalty_engine.payments.utils.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class ConfirmPaymentRespDto {

    @SerializedName("ResponseCode")
    private String responseCode;

    @SerializedName("ResponseDescription")
    private String responseDescription;

    @SerializedName("MerchantRequestID")
    private String merchantRequestID;

    @SerializedName("CheckoutRequestID")
    private String checkoutRequestID;

    @SerializedName("ResultCode")
    private String resultCode;

    @SerializedName("ResultDesc")
    private String resultDesc;

}
