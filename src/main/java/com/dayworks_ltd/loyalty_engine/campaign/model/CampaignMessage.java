package com.dayworks_ltd.loyalty_engine.campaign.model;

import com.dayworks_ltd.loyalty_engine.campaigns.CampaignEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class CampaignMessage {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long campaignMessageId;
    @Column( nullable = false )
    private String message;

//    @OneToMany( mappedBy = "campaignMessage" )
//    private List<CampaignMessageSent> messagesSent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private CampaignEntity campaign;

    @OneToMany(mappedBy = "campaignMessage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CampaignMessageSent> messagesSent;
}
