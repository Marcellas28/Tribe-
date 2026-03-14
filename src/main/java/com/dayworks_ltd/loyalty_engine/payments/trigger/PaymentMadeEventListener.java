package com.dayworks_ltd.loyalty_engine.payments.trigger;

import com.dayworks_ltd.loyalty_engine.customers.Customer;
import com.dayworks_ltd.loyalty_engine.payments.models.Payment;
import com.dayworks_ltd.loyalty_engine.campaign.service.Campaign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentMadeEventListener {

    @Autowired
    Campaign campaign;

    @EventListener
    public void handlePaymentMadeEvent( PaymentMadeEvent event )
    {
        Payment payment = event.getPayment();

        Customer customer = payment.getCustomer();


        System.out.println("payment = " + payment.getAmountPaid());
        if( payment.getAmountPaid() >= 0 /*500*/ )//only send thank you to customers
        {                                   //who pay for goods worth Ksh 500 or more
            campaign.sendThankYouNote( customer );
        }

    }
}
