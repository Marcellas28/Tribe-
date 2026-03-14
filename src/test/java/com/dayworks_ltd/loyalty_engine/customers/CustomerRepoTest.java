package com.dayworks_ltd.loyalty_engine.customers;

import com.dayworks_ltd.loyalty_engine.dto.CustomerDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomerRepoTest {
    @Autowired
    private CustomerRepo customerRepo;

    @Test
    void addCustomer()
    {
        Customer customer = Customer.builder()
                .phoneNumber("254708613719")
                .name("Ronald")
                .totalLitres(170.0)
                .lastTransaction(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        customerRepo.save( customer );

        customer = Customer.builder()
                .phoneNumber("254791013197")
                .name("Unknown Customer")
                .totalLitres(170.0)
                .lastTransaction(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        customerRepo.save( customer );

        customer = Customer.builder()
                .phoneNumber("254712696965")
                .name("Daniel Macharia")
                .totalLitres(170.0)
                .lastTransaction(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        customerRepo.save( customer );

        customer = Customer.builder()
                .phoneNumber("254757255732")
                .name("David")
                .totalLitres(170.0)
                .lastTransaction(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        customerRepo.save( customer );

        Logger.getAnonymousLogger().log(Level.FINE, "customer = " + customer.getName());
    }

    @Test
    void getAllLostCustomers() {
        List<CustomerDto> customers = customerRepo.getAllLostCustomers(LocalDateTime.now().minusHours(1L).toString());

        for( CustomerDto customer : customers )
        {
            System.out.println("Customer: " + customer.getName());
        }
    }
}