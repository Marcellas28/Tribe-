package com.dayworks_ltd.loyalty_engine.credit_engine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SupplierDTO {

    @JsonProperty("name")
    private String name;

    @JsonProperty("pin")
    private String pin;

    @JsonProperty("phone")
    private String phone;
}
