package com.dayworks_ltd.loyalty_engine.payments.trigger;

import com.dayworks_ltd.loyalty_engine.payments.models.Payment;
import com.dayworks_ltd.loyalty_engine.utility.SpringContext;
import jakarta.persistence.PostPersist;

public class PaymentEntityListener { //not a java bean

    @PostPersist
    public void afterInsertOnPayment(Payment payment) //listen for committed insert operations
    {                                                 //and publish the event
        //get bean via static context
        PaymentEventPublisher publisher = SpringContext.getBean(PaymentEventPublisher.class);

        publisher.publishPaymentMadeEvent( payment );
    }
}
