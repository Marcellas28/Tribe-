package com.dayworks_ltd.loyalty_engine.dto;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class CustomerDto {
    private Long customerId;
    private String phoneNumber;
    private String name;
    private Double totalLitres;
    private Timestamp lastTransaction;
    private Timestamp createdAt;

    private int loyaltyPoints;
}
