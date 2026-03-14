package com.dayworks_ltd.loyalty_engine.campaign.service;

import com.dayworks_ltd.loyalty_engine.campaign.repository.CampaignMessageRepository;
import com.dayworks_ltd.loyalty_engine.campaign.service.Campaign;
import com.dayworks_ltd.loyalty_engine.customers.Customer;
import com.dayworks_ltd.loyalty_engine.customers.CustomerRepo;
import com.dayworks_ltd.loyalty_engine.dto.CustomerDto;
import com.dayworks_ltd.loyalty_engine.campaign.model.CampaignMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class CampaignTest {

    @Autowired
    private Campaign campaign;

    @Autowired
    private CampaignMessageRepository messageRepository;

    @Autowired
    private CustomerRepo customerRepo;

    @Test
    void sendSMS() {
        CampaignMessage message = messageRepository.getCampaignMessageById(13L);
        List<Customer> customers = customerRepo.findAll();

        List<CustomerDto> customerData = customers.stream().map( customer -> CustomerDto.builder()
                        .customerId(customer.getCustomerId())
                        .phoneNumber(customer.getPhoneNumber())
                        .name(customer.getName())
                        .lastTransaction( new Timestamp(112345L))//customer.getLastTransaction())
                        .totalLitres(customer.getTotalLitres())
                        .loyaltyPoints(200)
                        .createdAt( new Timestamp(123456L))//customer.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        System.out.println("sending sms message: " + message.getMessage());

        campaign.sendSMSWithDto( customerData, message);
        System.out.println("after sending message..");
    }

    @Test
    void sendThankYouNote() {
        List<Customer> customers = customerRepo.findAll();

        for( Customer customer : customers )
        {
            campaign.sendThankYouNote(customer);
        }

    }

    @Test
    void sendOfferCampaigns() {
        CampaignMessage message = messageRepository.getCampaignMessageById(2L);
        campaign.sendOfferCampaigns(message);
    }
}