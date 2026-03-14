package com.dayworks_ltd.loyalty_engine.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CampaignSchedulerService {

    //fixedRate constants are set in the format: (days * hours * minutes * seconds * milliseconds)
    //should be five days. Using 4 seconds for simulation
    private static final long FIVE_DAYS_MILLIS = (4 * 1000); //( 5 * 24 * 60 * 60 * 1000);

    //should be 30 days. Using 12 seconds for simulation
    private static final long THIRTY_DAYS_MILLIS = (12 * 1000); //( 30 * 24 * 60 * 60 * 1000);

    //should be 7 days. Using 8 seconds for simulation
    private static final long SEVEN_DAYS_MILLIS = (8 * 1000); //( 8 * 24 * 60 * 60 * 1000);


    public boolean sendSMS(String[] contacts, String message)
    {
        try{
            System.out.println("\n\n\n");
//            new Thread(new Runnable() { //make the api call for sending sms on a separate thread.
//                @Override
//                public void run() {
                    for( String contact : contacts )
                    {
                        System.out.println("Sending: " + message + "\nTo: " + contact);
                    }
//                }
//            }).start();

            return true;
        } catch (Exception e) {
            System.out.println("Failed to send SMS");
            return false;
        }
    }

    //send thank you message to customer after visiting the station
    public void sendThankYouNote(String phoneNumber)
    {
        try{
            String message = "Thank you for fueling with us";

            String[] list = { phoneNumber };

            sendSMS( list, message);

        } catch (Exception e) {
            System.out.println("Failed to send thank you note !");
        }
    }

    //send campaigns for new products or offers
    public void sendOfferCampaigns(String campaignMessage)
    {
        try{
            //simulate sending of campaigns.
            //Query for all customers
            String[] contacts = {"Offer 4", "Offer 5", "Offer 6"};

            sendSMS( contacts, campaignMessage);
        } catch (Exception e) {
            System.out.println("Failed to send offer message");
        }
    }

    //Send campaigns after every five days
//    @Scheduled( fixedRate = FIVE_DAYS_MILLIS)
    private void scheduleLostCustomerCampaigns()
    {
        try{
            //simulate sending of campaigns.
            //Query for customers who have not visited the station for the last five days
            String[] contacts = {"Lost 4", "Lost 5", "Lost 6"};

            sendSMS( contacts, "It's been a while since you fueled with us. " +
                    "Visit us to enjoy new discounts");
        } catch (Exception e) {
            System.out.println("Failed to send campaigns to lost customers");
        }
    }

    //send campaigns for alternative products
//    @Scheduled( fixedRate = SEVEN_DAYS_MILLIS )
    private void scheduleAlternativeProductsCampaigns()
    {
        try{
            //simulate sending of campaigns.
            //Query for customers who consume particular products e.g fuel
            String[] contacts = {"alt 4", "alt 5", "alt 6"};
            String[] contacts_2 = {"alt 14", "alt 15", "alt 16"};

            sendSMS( contacts, "Need your car checked ? In need of maintenance services ?" +
                    "\nVisit us to get the best services.");

            sendSMS( contacts_2, "What tyres do you prefer ? How about engine oil ?" +
                    "\nCome and choose from the variety we have at affordable prices.");

        } catch (Exception e) {
            System.out.println("Failed to send campaigns for alternative products");
        }
    }

    //send campaigns after every one month. That is, every 30 days
//    @Scheduled( fixedRate = THIRTY_DAYS_MILLIS )
    private void scheduleLoyaltyPointsCampaigns()
    {
        try{
            //simulate sending of loyalty points campaigns.
            //Query for customers who have reached a loyalty points threshold
            //e.g those who have gained more than 1000 loyalty points
            String[] contacts = {"Loyal 4", "Loyal 5", "Loyal 6"};

            sendSMS( contacts, "Thank you for being our loyal customer. " +
                    "Visit us to receive any product worth Ksh 1,000 /=");
        } catch (Exception e) {
            System.out.println("Failed to send loyalty points campaigns");
        }
    }


}
