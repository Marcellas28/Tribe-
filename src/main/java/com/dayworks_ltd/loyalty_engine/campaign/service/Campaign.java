package com.dayworks_ltd.loyalty_engine.campaign.service;

import com.dayworks_ltd.loyalty_engine.campaign.repository.CampaignMessageRepository;
import com.dayworks_ltd.loyalty_engine.campaign.repository.CampaignMessageSentRepository;
import com.dayworks_ltd.loyalty_engine.Repository.LoyaltyPointsRepository;
import com.dayworks_ltd.loyalty_engine.customers.Customer;
import com.dayworks_ltd.loyalty_engine.customers.CustomerRepo;
import com.dayworks_ltd.loyalty_engine.dto.CustomerDto;
import com.dayworks_ltd.loyalty_engine.campaign.model.CampaignMessage;
import com.dayworks_ltd.loyalty_engine.payments.models.LoyaltyPoints;
import com.dayworks_ltd.loyalty_engine.utility.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class Campaign {

    @Value("${loyalty-engine.send-sms-api-endpoint}")
    private String apiEndPoint;

    @Value("${loyalty-engine.send-sms-sender-id}")
    private String senderId;

    @Value("${loyalty-engine.send-sms-api-key}")
    private String apiKey;

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private CampaignMessageSentRepository messageSentRepository;

    @Autowired
    private CampaignMessageRepository messageRepository;

    @Autowired
    private LoyaltyPointsRepository loyaltyPointsRepository;

    public boolean sendSMSMessage(ArrayList<Pair<String, String>> phoneNumberMessagePairList)
    {
        try
        {
            Logger.getAnonymousLogger().log(Level.FINE, "\n\n\n");
            StringBuilder requestBodyBuilder = new StringBuilder("{\n");
            requestBodyBuilder.append("\"senderID\":\"").append(senderId).append("\",\n");

            requestBodyBuilder.append("\"messageBody\":[\n");

            for( int i = 0; i < phoneNumberMessagePairList.size(); i++ ) {
                System.out.println("\nSending = " + phoneNumberMessagePairList.get(i).key + "\nMessage: " + phoneNumberMessagePairList.get(i).value);
                requestBodyBuilder.append("{\n");
                requestBodyBuilder.append("\"phone\":\"").append(phoneNumberMessagePairList.get(i).key).append("\",\n");
                requestBodyBuilder.append("\"message\":\"").append(phoneNumberMessagePairList.get(i).value).append("\"\n");

                requestBodyBuilder.append("}");

                if( i < (phoneNumberMessagePairList.size() - 1) )
                    requestBodyBuilder.append(",");

                requestBodyBuilder.append("\n");
            }

            requestBodyBuilder.append("\n]\n}");
            System.out.println("================== Sending Message=======================");
            System.out.println(requestBodyBuilder.toString());
            System.out.println("================== After sending Message =======================");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiEndPoint))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyBuilder.toString()))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if( response.statusCode() == 200 )//on success
            {
                System.out.println("Message send successful");
                return true;
            }

            Logger.getAnonymousLogger().log(Level.FINE, "Status Code: " + response.statusCode()
                    + "\nResponse Body: " + response.body());
            System.out.println("Status Code: " + response.statusCode()
                    + "\nResponse Body: " + response.body());


        } catch (Exception e) {
            Logger.getAnonymousLogger().log(Level.FINE, "Failed to send SMS");
        }
        return false;
    }

    public void sendSMSWithDto(List<CustomerDto> customers, CampaignMessage message)
    {
        try
        {
            Logger.getAnonymousLogger().log(Level.FINE, "\n\n\n");
            StringBuilder requestBodyBuilder = new StringBuilder("{\n");
            requestBodyBuilder.append("\"senderID\":\"").append(senderId).append("\",\n");

            requestBodyBuilder.append("\"messageBody\":[\n");

            for( int i = 0; i < customers.size(); i++ ) {
                System.out.println("\nThanking = " + customers.get(i).getPhoneNumber() + "\nMessage: " + message.getMessage());
                requestBodyBuilder.append("{\n");
                requestBodyBuilder.append("\"phone\":\"").append(customers.get(i).getPhoneNumber()).append("\",\n");


                //customize the campaign message here
                String messageToSend = message.getMessage();
//                messageToSend = messageToSend.replaceAll(":customer_name", customers.get(i).getName());
//                messageToSend = messageToSend.replaceAll(":loyalty_points", String.valueOf(customers.get(i).getLoyaltyPoints()));

                requestBodyBuilder.append("\"message\":\"").append(messageToSend).append("\"\n");

                requestBodyBuilder.append("}");

                if( i < (customers.size() - 1) )
                    requestBodyBuilder.append(",");

                requestBodyBuilder.append("\n");
            }

            requestBodyBuilder.append("\n]\n}");
            System.out.println("================== Sending Message=======================");
            System.out.println(requestBodyBuilder.toString());
            System.out.println("================== After sending Message =======================");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiEndPoint))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyBuilder.toString()))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if( response.statusCode() == 200 )//on success
            {
                System.out.println("Message send successful");
                //log message after sending
//                for( CustomerDto customer : customers )
//                {
//                    messageSentRepository.addCampaignMessageSent(message.getCampaignMessageId(), customer.getCustomerId());
//                }
            }

            Logger.getAnonymousLogger().log(Level.FINE, "Status Code: " + response.statusCode()
                    + "\nResponse Body: " + response.body());
            System.out.println("Status Code: " + response.statusCode()
                    + "\nResponse Body: " + response.body());
        } catch (Exception e) {
            Logger.getAnonymousLogger().log(Level.FINE, "Failed to send SMS");
        }
    }

    //send thank you message to customer after visiting the station
    public void sendThankYouNote(Customer customer)
    {
        try{
            System.out.println("Thanking customer = " + customer.getName());
            sendSMSWithDto(List.of(CustomerDto.builder()
                    .phoneNumber( customer.getPhoneNumber() )
                    .name( customer.getName() )
                    .loyaltyPoints( 0 )
                    .build()), CampaignMessage.builder().message("Thank you for purchasing our products. Welcome Again.").build());
            System.out.println("customer = " + customer.getName());
            CampaignMessage message = messageRepository.getCampaignMessageById(13L); //thank you notes is id 13

            LoyaltyPoints point = loyaltyPointsRepository
                    .findByCustomerAndRedeemedFalseAndMonthBefore(customer, YearMonth.now() )
                    .get(0);//get the first element only

            System.out.println("\n\npoints = " + point.getPoints() + "\n\n");

            CustomerDto customerDto = CustomerDto.builder()
                    .customerId( customer.getCustomerId() )
                    .phoneNumber( customer.getPhoneNumber() )
                    .name( customer.getName() )
                    .totalLitres( customer.getTotalLitres() )
                    .lastTransaction(Timestamp.valueOf(customer.getLastTransaction()))
                    .createdAt( Timestamp.valueOf( customer.getCreatedAt() ) )
                    .loyaltyPoints( point.getPoints() ) //This should be the number of points not redeemed
                    .build();

            List<CustomerDto> list = new ArrayList<>();
            list.add(customerDto);

            sendSMSWithDto( list, message);

        } catch (Exception e) {

            Logger.getAnonymousLogger().log( Level.FINE, "Failed to send thank you note !");
        }
    }

    //send campaigns for new products or offers
    public void sendOfferCampaigns(CampaignMessage campaignMessage)
    {
        try{
            //simulate sending of campaigns.
            //Query for all customers
            List<CustomerDto> customers = customerRepo.getAllCustomers();

            //sendSMSWithDto( customers, campaignMessage);
        } catch (Exception e) {
            Logger.getAnonymousLogger().log( Level.FINE, "Failed to send offer message");
        }
    }

    //Send campaigns daily
    @Scheduled( cron = "0 0 7 * * *")
    private void scheduleLostCustomerCampaigns()
    {
        try{
            //Query for customers who have not visited the station for the last five days
            List<CustomerDto> customers = customerRepo.getAllLostCustomers(LocalDateTime.now().minusDays(0).toString());
            CampaignMessage message = messageRepository.getCampaignMessageById(12L); //lost customer message is ID 12

            sendSMSWithDto( customers, message);
        } catch (Exception e) {
            Logger.getAnonymousLogger().log( Level.FINE, "Failed to send campaigns to lost customers");
        }
    }

    //send campaigns for alternative products every monday morning at 7:00 AM
   // @Scheduled( cron = "0 0 7 * * MON " ) //don't run this task for now
    private void scheduleAlternativeProductsCampaigns()
    {
        try{
            //Query for customers who consume particular products e.g fuel
            List<CustomerDto> customers = customerRepo.getAllCustomers();

            CampaignMessage message = messageRepository.getCampaignMessageById(14L);//offer message is ID 14. Not yet inserted!

            //sendSMSWithDto( customers, message);

        } catch (Exception e) {
            Logger.getAnonymousLogger().log( Level.FINE, "Failed to send campaigns for alternative products");
        }
    }

    //allow customers two days before start of point redemption window
    @Scheduled( cron = "0 0 7 24-31 * *") //run daily at 7:00 AM from 24th to 31st every month
    @Scheduled( cron = "0 0 7 1-3 * *") //run daily at 7:00 AM from 1st to 3rd every month
    private void scheduleLoyaltyPointsCampaigns()
    {
        try{
            //Query for all customers
            List<CustomerDto> customers = loyaltyPointsRepository.getAllCustomersWithLoyaltyPoints();

            CampaignMessage message = messageRepository.getCampaignMessageById(11L); //loyalty points message is ID 11

            sendSMSWithDto( customers, message);
        } catch (Exception e) {
            Logger.getAnonymousLogger().log( Level.FINE, "Failed to send loyalty points campaigns");
        }
    }

}
