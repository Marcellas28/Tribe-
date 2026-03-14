package com.dayworks_ltd.loyalty_engine.common;

import lombok.*;

import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ApiResponseBody {
    private String status;

    private String message;

    private Object respObject;
}
