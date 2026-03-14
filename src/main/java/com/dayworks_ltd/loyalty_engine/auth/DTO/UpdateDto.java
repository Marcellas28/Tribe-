package com.dayworks_ltd.loyalty_engine.auth.DTO;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class UpdateDto {
    private String id;
    private String attributeName;
    private String attributeValue;
}
