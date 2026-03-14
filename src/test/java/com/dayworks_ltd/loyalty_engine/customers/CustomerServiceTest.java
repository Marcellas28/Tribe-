package com.dayworks_ltd.loyalty_engine.customers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomerServiceTest {
    @Autowired
    CustomerService customerService;

    @Test
    void findOrCreateCustomer() {
        customerService.findOrCreateCustomer("0712696965", "Macharia");
        customerService.findOrCreateCustomer("0769050801", "Chirp");
    }
}