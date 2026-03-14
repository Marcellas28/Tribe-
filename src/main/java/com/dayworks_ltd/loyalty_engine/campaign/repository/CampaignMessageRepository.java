package com.dayworks_ltd.loyalty_engine.campaign.repository;

import com.dayworks_ltd.loyalty_engine.campaign.model.CampaignMessage;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignMessageRepository extends JpaRepository<CampaignMessage, Long> {

    @Modifying
    @Transactional
    @NativeQuery(
            value = "INSERT INTO campaign_message(message) " +
                    "VALUES (:message) "
    )
    int addCampaignMessage(@Param("message") String message);

    @Modifying
    @Transactional
    @NativeQuery(
            value = "INSERT INTO campaign_message(campaign_message_id, message) " +
                    "VALUES ( :messageId, :message) "
    )
    int addCampaignMessageWithId(@Param("messageId") Long messageId, @Param("message") String message);

    @NativeQuery(
            value = "SELECT * FROM campaign_message " +
                    "WHERE campaign_message_id = :messageId"
    )
    CampaignMessage getCampaignMessageById(@Param("messageId") Long campaignMessageId);

    @NativeQuery(
            value = "SELECT * FROM campaign_message"
    )
    List<CampaignMessage> getAllCampaignMessages();
}
