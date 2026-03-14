package com.dayworks_ltd.loyalty_engine.payments.utils.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IntiatePaymentDto {

    @SerializedName("BusinessShortCode")
    private String businessShortCode;

    @SerializedName("Password")
    private String password;

    @SerializedName("Timestamp")
    private String timeStamp;

    @SerializedName("TransactionType")
    private String transactionType;

    @SerializedName("Amount")
    private String amount;

    @SerializedName("PartyA")
    private String partyA;

    @SerializedName("PartyB")
    private String partyB;

    @SerializedName("PhoneNumber")
    private String phoneNumber;

    @SerializedName("CallBackURL")
    private String callbackUrl;

    @SerializedName("AccountReference")
    private String accountReference;

    @SerializedName("TransactionDesc")
    private String transactionDesc;
}
