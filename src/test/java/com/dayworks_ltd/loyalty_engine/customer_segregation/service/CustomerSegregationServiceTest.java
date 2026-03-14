package com.dayworks_ltd.loyalty_engine.customer_segregation.service;

import com.dayworks_ltd.loyalty_engine.customers.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class CustomerSegregationServiceTest {

    @Autowired
    private CustomerSegregationService segregationService;

    private final String tillNumber = "12345"; //this is the till inserted by the SegregationDataHelper test

    @Test
    void getAllNewCustomers() {
        List<Customer> newCustomers = segregationService.getAllNewCustomers(tillNumber);

        System.out.println("==================================New Customers=====================================");
        System.out.println("Total rows: " + newCustomers.size());
        for(Customer customer : newCustomers )
        {
            System.out.println("Name " + customer.getName() + "\tCustomer ID: " + customer.getCustomerId());
        }

        System.out.println("==================================End of New Customers=====================================");
    }

    @Test
    void getAllActiveCustomers() {
        List<Customer> activeCustomers = segregationService.getAllActiveCustomers(tillNumber);

        System.out.println("==================================Active Customers=====================================");
        System.out.println("Total rows: " + activeCustomers.size());
        for(Customer customer : activeCustomers )
        {
            System.out.println("Name " + customer.getName() + "\tCustomer ID: " + customer.getCustomerId());
        }

        System.out.println("==================================End of Active Customers=====================================");
    }

    @Test
    void getAllLoyalCustomers() {
        List<Customer> loyalCustomers = segregationService.getAllLoyalCustomers(tillNumber);

        System.out.println("==================================Loyal Customers=====================================");
        System.out.println("Total rows: " + loyalCustomers.size());
        for(Customer customer : loyalCustomers )
        {
            System.out.println("Name " + customer.getName() + "\tCustomer ID: " + customer.getCustomerId());
        }

        System.out.println("==================================End of Loyal Customers=====================================");
    }

    @Test
    void getAllDormantCustomers() {
        List<Customer> dormantCustomers = segregationService.getAllDormantCustomers(tillNumber);

        System.out.println("==================================Dormant Customers=====================================");
        System.out.println("Total rows: " + dormantCustomers.size());
        for(Customer customer : dormantCustomers )
        {
            System.out.println("Name " + customer.getName() + "\tCustomer ID: " + customer.getCustomerId());
        }

        System.out.println("==================================End of Dormant Customers=====================================");
    }

    @Test
    void getAllChurnedCustomers() {
        List<Customer> churnedCustomers = segregationService.getAllChurnedCustomers(tillNumber);

        System.out.println("==================================Churned Customers=====================================");
        System.out.println("Total rows: " + churnedCustomers.size());
        for(Customer customer : churnedCustomers )
        {
            System.out.println("Name " + customer.getName() + "\tCustomer ID: " + customer.getCustomerId());
        }

        System.out.println("==================================End of Churned Customers=====================================");
    }

    @Test
    void getLowSpendingCustomers() {
        List<Customer> lowSpendingCustomers = segregationService.getLowSpendingCustomers(tillNumber);

        System.out.println("==================================Low Spending Customers=====================================");
        System.out.println("Total rows: " + lowSpendingCustomers.size());
        for(Customer customer : lowSpendingCustomers )
        {
            System.out.println("Name " + customer.getName() + "\tCustomer ID: " + customer.getCustomerId());
        }

        System.out.println("==================================End of Low Spending Customers=====================================");
    }

    @Test
    void getMediumSpendingCustomers() {
        List<Customer> mediumSpendingCustomers = segregationService.getMediumSpendingCustomers(tillNumber);

        System.out.println("==================================Medium Spending Customers=====================================");
        System.out.println("Total rows: " + mediumSpendingCustomers.size());
        for(Customer customer : mediumSpendingCustomers )
        {
            System.out.println("Name " + customer.getName() + "\tCustomer ID: " + customer.getCustomerId());
        }

        System.out.println("==================================End of Medium Spending Customers=====================================");
    }

    @Test
    void getHighSpendingCustomers() {
        List<Customer> highSpendingCustomers = segregationService.getHighSpendingCustomers(tillNumber);

        System.out.println("==================================High Spending Customers=====================================");
        System.out.println("Total rows: " + highSpendingCustomers.size());
        for(Customer customer : highSpendingCustomers )
        {
            System.out.println("Name " + customer.getName() + "\tCustomer ID: " + customer.getCustomerId());
        }

        System.out.println("==================================End of High Spending Customers=====================================");
    }

    @Test
    void getVIPCustomers() {
        List<Customer> vipCustomers = segregationService.getVIPCustomers(tillNumber);

        System.out.println("==================================VIP Customers=====================================");
        System.out.println("Total rows: " + vipCustomers.size());
        for(Customer customer : vipCustomers )
        {
            System.out.println("Name " + customer.getName() + "\tCustomer ID: " + customer.getCustomerId());
        }

        System.out.println("==================================End of VIP Customers=====================================");
    }

    @Test
    void getFrequentCustomers() {
        List<Customer> frequentCustomers = segregationService.getFrequentCustomers(tillNumber);

        System.out.println("==================================Frequent Customers=====================================");
        System.out.println("Total rows: " + frequentCustomers.size());
        for(Customer customer : frequentCustomers )
        {
            System.out.println("Name " + customer.getName() + "\tCustomer ID: " + customer.getCustomerId());
        }

        System.out.println("==================================End of Frequent Customers=====================================");
    }

    @Test
    void getBulkBuyingCustomers() {
        List<Customer> bulkBuyingCustomers = segregationService.getBulkBuyingCustomers(tillNumber);

        System.out.println("==================================Bulk Buying Customers=====================================");
        System.out.println("Total rows: " + bulkBuyingCustomers.size());
        for(Customer customer : bulkBuyingCustomers )
        {
            System.out.println("Name " + customer.getName() + "\tCustomer ID: " + customer.getCustomerId());
        }

        System.out.println("==================================End of Bulk Buying Customers=====================================");
    }

    @Test
    void getOneTimeBuyingCustomers() {
        List<Customer> oneTimeBuyingCustomers = segregationService.getOneTimeBuyingCustomers(tillNumber);

        System.out.println("==================================One-time Buying Customers=====================================");
        System.out.println("Total rows: " + oneTimeBuyingCustomers.size());
        for(Customer customer : oneTimeBuyingCustomers )
        {
            System.out.println("Name " + customer.getName() + "\tCustomer ID: " + customer.getCustomerId());
        }

        System.out.println("==================================End of One-time Buying Customers=====================================");
    }

    @Test
    void getRecoveringCustomers() {
        List<Customer> recoveringCustomers = segregationService.getRecoveringCustomers(tillNumber);

        System.out.println("==================================Recovering Customers=====================================");
        System.out.println("Total rows: " + recoveringCustomers.size());
        for(Customer customer : recoveringCustomers )
        {
            System.out.println("Name " + customer.getName() + "\tCustomer ID: " + customer.getCustomerId());
        }

        System.out.println("==================================End of Recovering Customers=====================================");
    }

    @Test
    void getDecliningCustomers() {
        List<Customer> decliningCustomers = segregationService.getDecliningCustomers(tillNumber);

        System.out.println("==================================Declining Customers=====================================");
        System.out.println("Total rows: " + decliningCustomers.size());
        for(Customer customer : decliningCustomers )
        {
            System.out.println("Name " + customer.getName() + "\tCustomer ID: " + customer.getCustomerId());
        }

        System.out.println("==================================End Declining Customers=====================================");
    }

    @Test
    void getConsistentCustomers() {
        List<Customer> consistentCustomers = segregationService.getConsistentCustomers(tillNumber);

        System.out.println("==================================Consistent Customers=====================================");
        System.out.println("Total rows: " + consistentCustomers.size());
        for(Customer customer : consistentCustomers )
        {
            System.out.println("Name " + customer.getName() + "\tCustomer ID: " + customer.getCustomerId());
        }

        System.out.println("==================================End Consistent Customers=====================================");
    }
}