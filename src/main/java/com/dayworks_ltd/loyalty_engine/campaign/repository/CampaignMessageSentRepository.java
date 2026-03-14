package com.dayworks_ltd.loyalty_engine.campaign.repository;

import com.dayworks_ltd.loyalty_engine.campaign.dto.CampaignMessageDto;
import com.dayworks_ltd.loyalty_engine.campaign.model.CampaignMessageSent;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignMessageSentRepository extends JpaRepository<CampaignMessageSent, Long> {
    @Modifying
    @Transactional
    @NativeQuery(
            value = "INSERT INTO campaign_message_sent( campaign_message_id, id ) " +
                    "VALUES(:campaignMessageId, :customerId)"
    )
    int addCampaignMessageSent(@Param("campaignMessageId") Long campaignMessageId,
                               @Param("customerId") Long customerId );

    @NativeQuery(
            value = "SELECT message FROM campaign_message cm " +
                    "JOIN campaign_message_sent cms " +
                    "ON cms.campaign_message_id = cm.campaign_message_id " +
                    "WHERE cms.id = :customerId"
    )
    List<CampaignMessageDto> getAllMessagesSentToCustomer( @Param("customerId") Long customerId );

}
