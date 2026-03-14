package com.dayworks_ltd.loyalty_engine.credit_engine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class InvoiceExtractionResponse {

    @JsonProperty("invoiceSubmissionId")
    private String invoiceSubmissionId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("data")
    private InvoiceDataDTO data;

    @JsonProperty("error")
    private String error;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @JsonProperty("processedAt")
    private LocalDateTime processedAt;
}
