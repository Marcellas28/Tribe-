package com.dayworks_ltd.loyalty_engine.utility;

import com.dayworks_ltd.loyalty_engine.campaign.repository.CampaignMessageRepository;
import com.dayworks_ltd.loyalty_engine.campaign.model.CampaignMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class MessageLoader implements CommandLineRunner {
    //get messages from the application properties file
    @Value("${loyalty-engine.redeemLoyaltyPoints}")
    private String redeemLoyaltyPointsMessage;
    @Value("${loyalty-engine.reachLostCustomer}")
    private String reachLostCustomerMessage;
    @Value("${loyalty-engine.thankYouNote}")
    private String thankYouNoteMessage;

    @Autowired
    private CampaignMessageRepository messageRepository;

    @Override
    public void run(String ...args) throws Exception
    {
        /*
        try{
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

        }
        catch (Exception e)
        {
            Logger.getAnonymousLogger().log(Level.FINE, "messages aready present in db");
        }
    }
    */

//        addMessageIfAbsent(11L, redeemLoyaltyPointsMessage);
//        addMessageIfAbsent(12L, reachLostCustomerMessage);
//        addMessageIfAbsent(13L, thankYouNoteMessage);
    }

        private void addMessageIfAbsent(Long id, String messageContent) {
        try {
            if (!messageRepository.existsById(id)) {
                CampaignMessage message = CampaignMessage.builder()
                        .message(messageContent)
                        .build();
                messageRepository.addCampaignMessageWithId(id, message.getMessage());
            } else {
                Logger.getAnonymousLogger().log(Level.FINE, "Message with ID " + id + " already exists");
            }
        } catch (Exception e) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Error checking or adding message with ID " + id, e);
        }
    }
}
