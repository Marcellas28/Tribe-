package com.dayworks_ltd.loyalty_engine.Repository;

import com.dayworks_ltd.loyalty_engine.campaign.model.CampaignMessage;
import com.dayworks_ltd.loyalty_engine.campaign.repository.CampaignMessageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootTest
class CampaignMessageRepositoryTest {

    //get messages from the application properties file
    @Value("${loyalty-engine.redeemLoyaltyPoints}")
    private String redeemLoyaltyPointsMessage;
    @Value("${loyalty-engine.reachLostCustomer}")
    private String reachLostCustomerMessage;
    @Value("${loyalty-engine.thankYouNote}")
    private String thankYouNoteMessage;
//    @Value("${loyalty-engine.offer}")
//    private String offerMessage;

    @Autowired
    CampaignMessageRepository messageRepository;

    @Test
    void addCampaignMessage() {
        CampaignMessage message = CampaignMessage.builder()
                .message(redeemLoyaltyPointsMessage)
                .build();
        messageRepository.addCampaignMessageWithId( 11L, message.getMessage() ); //redeem loyalty points message use id 11

        message = CampaignMessage.builder()
                .message(reachLostCustomerMessage)
                .build();
        messageRepository.addCampaignMessageWithId( 12L, message.getMessage() ); //reach lost customer messages use id 12

        message = CampaignMessage.builder()
                .message(thankYouNoteMessage)
                .build();
        messageRepository.addCampaignMessageWithId( 13L, message.getMessage() ); //thank you notes use ID 13

//        message = CampaignMessage.builder()
//                .message(offerMessage)
//                .build();
//        messageRepository.addCampaignMessageWithId( 14L, message.getMessage() ); //offer messages use ID 14
    }

    @Test
    void getCampaignMessageById() {
        CampaignMessage message = messageRepository.getCampaignMessageById(13L);

        Logger.getAnonymousLogger().log( Level.FINE, "message = " + message.getMessage());
    }

    @Test
    void getAllCampaignMessages() {
        List<CampaignMessage> messages = messageRepository.getAllCampaignMessages();

        for( CampaignMessage message : messages )
        {
            //Logger.getAnonymousLogger().log( Level.FINE, "message = " + message.getMessage());
            System.out.println("message = " + message.getMessage());
        }
    }
}