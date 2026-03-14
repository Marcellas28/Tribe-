package com.dayworks_ltd.loyalty_engine.payments.utils.dto;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
@Data

public class C2BCallbackDto {

    @SerializedName("TransactionType")
    private String transactionType;

    @SerializedName("TransID")
    private String transID;

    @SerializedName("TransTime")
    private String transTime;

    @SerializedName("TransAmount")
    private String transAmount;

    @SerializedName("BusinessShortCode")
    private String businessShortCode;

    @SerializedName("BillRefNumber")
    private String billRefNumber;

    @SerializedName("InvoiceNumber")
    private String invoiceNumber;

    @SerializedName("OrgAccountBalance")
    private String orgAccountBalance;

    @SerializedName("ThirdPartyTransID")
    private String thirdPartyTransID;

    @SerializedName("MSISDN")
    private String msisdn;

    @SerializedName("FirstName")
    private String firstName;

    @SerializedName("MiddleName")
    private String middleName;

    @SerializedName("LastName")
    private String lastName;
}
