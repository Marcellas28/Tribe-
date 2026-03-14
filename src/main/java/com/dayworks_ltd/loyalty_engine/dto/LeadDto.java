package com.dayworks_ltd.loyalty_engine.dto;
import com.dayworks_ltd.loyalty_engine.common.customersType;


import lombok.Data;

@Data

public class LeadDto {

    private String name;
    private String gender;
    private String locality;
    private String phoneNumber;
    private customersType customerType;
    private String collectedBy;
}
