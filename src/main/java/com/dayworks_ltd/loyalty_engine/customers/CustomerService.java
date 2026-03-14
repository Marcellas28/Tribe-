package com.dayworks_ltd.loyalty_engine.customers;

import com.dayworks_ltd.loyalty_engine.common.customersType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CustomerService {
    @Autowired
    CustomerRepo customerRepo;

    public Customer findOrCreateCustomer(String phoneNumber, String name) {
        return customerRepo.findByPhoneNumber(phoneNumber)
                .orElseGet(() -> {
                    Customer newCustomer = new Customer();
                    newCustomer.setPhoneNumber(phoneNumber);
                    newCustomer.setName(name);
                    newCustomer.setTotalLitres(0.0);
                    newCustomer.setCustomerType(customersType.PERSONAL);
                    //newCustomer.setTotalPoints(0);
                    newCustomer.setCreatedAt(LocalDateTime.now());
                    return customerRepo.save(newCustomer);
                });
    }

    // Add this method to CustomerService
    public Customer saveCustomer(Customer customer) {
        return customerRepo.save(customer);
    }
}
