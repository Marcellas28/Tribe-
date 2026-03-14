package com.dayworks_ltd.loyalty_engine.campaigns;

import com.dayworks_ltd.loyalty_engine.campaign.model.CampaignMessage;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "campaigns")
@Data
public class CampaignEntity {  // Renamed to singular + uppercase

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "campaign_name", nullable = false)
    private String campaignName;

    @NotBlank
    @Column(name = "campaign_type", nullable = false)
    private String campaignType; // Discount, Double Points, Cashback, etc.

    @NotBlank
    @Column(name = "target_audience", nullable = false)
    private String targetAudience; // All, New, Spending > X, etc.

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "created_at", updatable = false)
    private LocalDate createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDate.now();
    }

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CampaignMessage> messages;
}
