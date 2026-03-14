package com.dayworks_ltd.loyalty_engine.credit_engine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
public class InvoiceDataDTO
{
    @JsonProperty("supplier")
    private SupplierDTO supplier;

    @JsonProperty("invoice")
    private InvoiceHeaderDTO invoice;

    @JsonProperty("items")
    private List<InvoiceItemDTO> items;

    @JsonProperty("confidence")
    private BigDecimal confidence;
}
