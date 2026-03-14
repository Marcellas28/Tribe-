package com.dayworks_ltd.loyalty_engine.credit_engine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class InvoiceItemDTO {
    @JsonProperty("rawName")
    private String rawName;

    @JsonProperty("normalizedName")
    private String normalizedName;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("unitCost")
    private BigDecimal unitCost;

    @JsonProperty("lineTotal")
    private BigDecimal lineTotal;
}
