package com.dayworks_ltd.loyalty_engine.Repository;

import com.dayworks_ltd.loyalty_engine.campaign.dto.CampaignMessageDto;
import com.dayworks_ltd.loyalty_engine.campaign.repository.CampaignMessageSentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootTest
class
CampaignMessageSentRepositoryTest {

    @Autowired
    private CampaignMessageSentRepository messageRepository;

    @Test
    void addCampaignMessageSent() {
        messageRepository.addCampaignMessageSent(2L,4L);
        messageRepository.addCampaignMessageSent(1L,4L);

        messageRepository.addCampaignMessageSent(3L,5L);
    }

    @Test
    void getAllMessagesSentToCustomer() {

        List<CampaignMessageDto> messages = messageRepository.getAllMessagesSentToCustomer(5L);

        Logger.getAnonymousLogger().log( Level.FINE, "Messages sent to First customer");
        for( CampaignMessageDto message : messages )
        {
            Logger.getAnonymousLogger().log( Level.FINE, "message = " + message.getMessage());
        }

        messages = messageRepository.getAllMessagesSentToCustomer(4L);
        System.out.println("Messages sent to Second customer");
        for( CampaignMessageDto message : messages )
        {
            Logger.getAnonymousLogger().log( Level.FINE, "message = " + message.getMessage());
        }

    }
}