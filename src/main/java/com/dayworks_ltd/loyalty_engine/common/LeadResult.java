package com.dayworks_ltd.loyalty_engine.common;

import com.dayworks_ltd.loyalty_engine.customers.Customer;

public class LeadResult {
    private final Customer customer;
    private final boolean isNew;

    public LeadResult(Customer customer, boolean isNew) {
        this.customer = customer;
        this.isNew = isNew;
    }

    public Customer getCustomer() { return customer; }
    public boolean isNew() { return isNew; }
}
