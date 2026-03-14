package com.dayworks_ltd.loyalty_engine.payments.models;

import com.dayworks_ltd.loyalty_engine.customers.Customer;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.YearMonth;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class LoyaltyPoints {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn( name = "customer_id", nullable = false)
    private Customer customer;

    private int points;  // <-- make sure this field exists and spelled exactly as used

    private YearMonth month;

    private boolean redeemed;

    private LocalDateTime awardedAt;
}
