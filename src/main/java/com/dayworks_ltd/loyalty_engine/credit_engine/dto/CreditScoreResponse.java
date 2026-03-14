package com.dayworks_ltd.loyalty_engine.credit_engine.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreditScoreResponse {
    private String merchant_id;
    private Integer score;
    private String grade;
    private boolean is_provisional;
    private String calculated_on;
    private DataPeriod data_period;
    private JsonNode breakdown;
}