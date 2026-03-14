package com.dayworks_ltd.loyalty_engine.campaign.model;

import com.dayworks_ltd.loyalty_engine.customers.Customer;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class CampaignMessageSent {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long campaignMessageSentId;

    @ManyToOne
    @JoinColumn( name = "campaignMessageId")
    private CampaignMessage campaignMessage;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

//    @ManyToOne
//    @JoinColumn( name = "id", nullable = false)
//    private Customer customer;
}
