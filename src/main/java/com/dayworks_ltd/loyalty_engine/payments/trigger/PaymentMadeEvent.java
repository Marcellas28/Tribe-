package com.dayworks_ltd.loyalty_engine.payments.trigger;

import com.dayworks_ltd.loyalty_engine.payments.models.Payment;

public class PaymentMadeEvent { //wrapper class for the payment insert operation

    private final Payment payment;

    public PaymentMadeEvent( Payment payment )
    {
        this.payment = payment;
    }

    public Payment getPayment() {
        return this.payment;
    }
}
