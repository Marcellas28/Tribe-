package com.dayworks_ltd.loyalty_engine.auth.DTO;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class AuthToken {
    private String jwt;
}
