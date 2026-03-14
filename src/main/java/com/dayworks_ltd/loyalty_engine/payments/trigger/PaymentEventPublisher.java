package com.dayworks_ltd.loyalty_engine.payments.trigger;

import com.dayworks_ltd.loyalty_engine.payments.models.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventPublisher {

    @Autowired
    private ApplicationEventPublisher publisher;

    public void publishPaymentMadeEvent(Payment payment) //publish the payment insert event
    {                                                    //making it available to all event listeners
        publisher.publishEvent( new PaymentMadeEvent(payment) ); //in the spring application
    }

}
