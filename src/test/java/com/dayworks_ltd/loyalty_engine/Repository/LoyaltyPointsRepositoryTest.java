package com.dayworks_ltd.loyalty_engine.Repository;

import com.dayworks_ltd.loyalty_engine.customers.Customer;
import com.dayworks_ltd.loyalty_engine.customers.CustomerRepo;
import com.dayworks_ltd.loyalty_engine.dto.CustomerDto;
import com.dayworks_ltd.loyalty_engine.payments.models.LoyaltyPoints;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoyaltyPointsRepositoryTest {

    @Autowired
    private LoyaltyPointsRepository repository;
    @Autowired
    private CustomerRepo repo;


    @Test
    void addLoyaltyPoints()
    {
        List<Customer> customers = repo.findAll();

        for( Customer customer : customers )
        {
            LoyaltyPoints loyaltyPoints = LoyaltyPoints.builder()
                    .points(100 + (int)( Math.random() * 900 ) )
                    .month(YearMonth.now())
                    .redeemed(false)
                    .awardedAt(LocalDateTime.now())
                    .customer( customer )
                    .build();

            repository.save(loyaltyPoints);
            System.out.println("loyaltyPoints = " + loyaltyPoints.getPoints());
        }
    }
    @Test
    void getAllCustomersWithSufficientLoyaltyPoints() {

        List<Customer> customers = repository.getAllCustomersWithSufficientLoyaltyPoints();

        for( Customer customer : customers )
        {
            System.out.println("customer = " + customer.getName());
        }
    }

    @Test
    void getAllCustomersWithLoyaltyPoints() {
        List<CustomerDto> customers = repository.getAllCustomersWithLoyaltyPoints();

        for( CustomerDto customer : customers )
        {
            System.out.println("customer: " + customer.getName() + "  Points: " + customer.getLoyaltyPoints());
        }
    }
}