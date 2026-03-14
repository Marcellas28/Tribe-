package com.dayworks_ltd.loyalty_engine.credit_engine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
public class InvoiceHeaderDTO {
    @JsonProperty("invoiceNumber")
    private String invoiceNumber;

    @JsonProperty("date")
    private LocalDate date;

    @JsonProperty("subtotal")
    private BigDecimal subtotal;

    @JsonProperty("vat")
    private BigDecimal vat;

    @JsonProperty("total")
    private BigDecimal total;
}
