package com.dayworks_ltd.loyalty_engine.customer_segregation.service;


import com.dayworks_ltd.loyalty_engine.customer_segregation.enums.CustomerSegment;
import com.dayworks_ltd.loyalty_engine.customers.Customer;
import com.dayworks_ltd.loyalty_engine.customers.CustomerRepo;
import com.dayworks_ltd.loyalty_engine.payments.PaymentRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerSegregationService {
    private static final Logger logger = LoggerFactory.getLogger(CustomerSegregationService.class);

    private final PaymentRepo paymentRepository;
    private final CustomerRepo customerRepository;

    public CustomerSegregationService(PaymentRepo paymentRepository, CustomerRepo customerRepository)
    {
        this.paymentRepository = paymentRepository;
        this.customerRepository = customerRepository;
    }

    //USE THIS METHOD TO EXPOSE CUSTOMERS IN SPECIFIC SEGMENTS
    private List<Customer> getCustomersInSegment(String customerSegment)
    {
        Optional<List<Customer>> customersInSegment = customerRepository.getCustomersInSegment(customerSegment);

        return customersInSegment.orElseGet(ArrayList::new);
    }

    @Transactional
    private void updateCustomersToSegment(List<Customer> customers, String newCustomerSegment)
    {
        logger.info("\n\n");
        logger.info("=================================Updating customers to segment: " + newCustomerSegment + "========================");

        for( Customer customer : customers )
        {
            customerRepository.updateCustomerSegment(customer.getCustomerId(), newCustomerSegment);
        }

        logger.info("=================================Finished Updating customers to segment: " + newCustomerSegment + "========================");
        logger.info("\n\n");
    }


    List<Customer> getAllNewCustomers(String tillNumber)//a new customer has either made a single payment to the till,
    {                                                   //or was created in the last seven days

        logger.info("\n\n");
        logger.info("=================================Getting New Customers=========================");

        Optional<List<Customer>> singlePaymentCustomers = paymentRepository.getSinglePaymentCustomers(tillNumber);
        List<Customer> customerList = new ArrayList<>();

        Optional<List<Customer>> sevenDayOldCustomers = customerRepository.getAllCustomersCreatedWithinLastSevenDays(tillNumber);

        singlePaymentCustomers.ifPresent(customerList::addAll);

        sevenDayOldCustomers.ifPresent(customerList::addAll);

        updateCustomersToSegment(customerList, CustomerSegment.NEW_CUSTOMER.name());

        logger.info("=================================Finished Getting New Customers=========================");
        logger.info("\n\n");

        return customerList;
    }

    List<Customer> getAllActiveCustomers(String tillNumber)
    {
        logger.info("\n\n");
        logger.info("=================================Getting Active Customers=========================");

        Optional<List<Customer>> activeCustomers = customerRepository.getAllCustomersCreatedWithinLastFourteenDaysAndHavePaidMoreThanTwice(tillNumber);

        List<Customer> customerList = new ArrayList<>();

        if( activeCustomers.isPresent() )
        {
            customerList = activeCustomers.get();
            updateCustomersToSegment(activeCustomers.get(), CustomerSegment.ACTIVE_CUSTOMER.name());
        }

        logger.info("=================================Finished Getting Active Customers=========================");
        logger.info("\n\n");

        return customerList;
    }

    List<Customer> getAllLoyalCustomers(String tillNumber)
    {
        logger.info("\n\n");
        logger.info("=================================Getting Loyal Customers=========================");

        Optional<List<Customer>> loyalCustomers = paymentRepository.getAllLoyalCustomers(tillNumber);

        List<Customer> customerList = new ArrayList<>();

        if( loyalCustomers.isPresent() )
        {
            customerList = loyalCustomers.get();
            updateCustomersToSegment(loyalCustomers.get(), CustomerSegment.LOYAL_CUSTOMER.name());
        }

        logger.info("=================================Finished Getting Loyal Customers=========================");
        logger.info("\n\n");

        return customerList;
    }

    List<Customer> getAllDormantCustomers(String tillNumber)
    {
        logger.info("\n\n");
        logger.info("=================================Getting Dormant Customers=========================");

        Optional<List<Customer>> dormantCustomers = customerRepository.getDormantCustomers(tillNumber);

        List<Customer> customerList = new ArrayList<>();

        if( dormantCustomers.isPresent() )
        {
            customerList = dormantCustomers.get();
            updateCustomersToSegment(dormantCustomers.get(), CustomerSegment.DORMANT_CUSTOMER.name());
        }

        logger.info("=================================Finished Getting Dormant Customers=========================");
        logger.info("\n\n");

        return customerList;
    }

    List<Customer> getAllChurnedCustomers(String tillNumber)
    {
        logger.info("\n\n");
        logger.info("=================================Getting Churned Customers=========================");

        Optional<List<Customer>> churnedCustomers = customerRepository.getChurnedCustomers(tillNumber);

        List<Customer> customerList = new ArrayList<>();

        if( churnedCustomers.isPresent() )
        {
            customerList = churnedCustomers.get();
            updateCustomersToSegment(churnedCustomers.get(), CustomerSegment.CHURNED_CUSTOMER.name());
        }

        logger.info("=================================Finished Getting Churned Customers=========================");
        logger.info("\n\n");

        return customerList;
    }

    List<Customer> getLowSpendingCustomers(String tillNumber)
    {
        logger.info("\n\n");
        logger.info("=================================Getting Low Spending Customers=========================");

        Optional<List<Customer>> lowSpendingCustomers = paymentRepository.getLowSpendingCustomers(tillNumber);

        List<Customer> customerList = new ArrayList<>();

        if( lowSpendingCustomers.isPresent() )
        {
            customerList = lowSpendingCustomers.get();
            updateCustomersToSegment(lowSpendingCustomers.get(), CustomerSegment.LOW_SPENDER.name());
        }

        logger.info("=================================Finished Getting Low Spending Customers=========================");
        logger.info("\n\n");

        return customerList;
    }

    List<Customer> getMediumSpendingCustomers(String tillNumber)
    {
        logger.info("\n\n");
        logger.info("=================================Getting Medium Spending Customers=========================");

        Optional<List<Customer>> mediumSpendingCustomers = paymentRepository.getMediumSpendingCustomers(tillNumber);

        List<Customer> customerList = new ArrayList<>();

        if( mediumSpendingCustomers.isPresent() )
        {
            customerList = mediumSpendingCustomers.get();
            updateCustomersToSegment(mediumSpendingCustomers.get(), CustomerSegment.MEDIUM_SPENDER.name());
        }

        logger.info("=================================Finished Getting Medium Spending Customers=========================");
        logger.info("\n\n");

        return customerList;
    }

    List<Customer> getHighSpendingCustomers(String tillNumber)
    {
        logger.info("\n\n");
        logger.info("=================================Getting High Spending Customers=========================");

        Optional<List<Customer>> highSpendingCustomers = paymentRepository.getHighSpendingCustomers(tillNumber);

        List<Customer> customerList = new ArrayList<>();

        if( highSpendingCustomers.isPresent() )
        {
            customerList = highSpendingCustomers.get();
            updateCustomersToSegment(highSpendingCustomers.get(), CustomerSegment.HIGH_SPENDER.name());
        }

        logger.info("=================================Finished Getting High Spending Customers=========================");
        logger.info("\n\n");

        return customerList;
    }

    List<Customer> getVIPCustomers(String tillNumber)
    {
        logger.info("\n\n");
        logger.info("=================================Getting VIP Customers=========================");

        Optional<List<Customer>> vipCustomers = paymentRepository.getVIPCustomers(tillNumber);

        List<Customer> customerList = new ArrayList<>();

        if( vipCustomers.isPresent() )
        {
            customerList = vipCustomers.get();
            updateCustomersToSegment(vipCustomers.get(), CustomerSegment.VIP.name());
        }

        logger.info("=================================Finished Getting VIP Customers=========================");
        logger.info("\n\n");

        return customerList;
    }

    List<Customer> getFrequentCustomers(String tillNumber)
    {
        logger.info("\n\n");
        logger.info("=================================Getting Frequent Customers=========================");

        Optional<List<Customer>> frequentCustomers = paymentRepository.getFrequentCustomers(tillNumber);

        List<Customer> customerList = new ArrayList<>();

        if( frequentCustomers.isPresent() )
        {
            customerList = frequentCustomers.get();
            updateCustomersToSegment(frequentCustomers.get(), CustomerSegment.FREQUENT_BUYER.name());
        }

        logger.info("=================================Finished Getting Frequent Customers=========================");
        logger.info("\n\n");

        return customerList;
    }

    List<Customer> getBulkBuyingCustomers(String tillNumber)
    {
        logger.info("\n\n");
        logger.info("=================================Getting Bulk Buying Customers=========================");

        Optional<List<Customer>> bulkBuyingCustomers = paymentRepository.getBulkBuyingCustomers(tillNumber);

        List<Customer> customerList = new ArrayList<>();

        if( bulkBuyingCustomers.isPresent() )
        {
            customerList = bulkBuyingCustomers.get();
            updateCustomersToSegment(bulkBuyingCustomers.get(), CustomerSegment.BULK_BUYER.name());
        }

        logger.info("=================================Finished Getting Bulk Buying Customers=========================");
        logger.info("\n\n");

        return customerList;
    }

    List<Customer> getOneTimeBuyingCustomers(String tillNumber)
    {
        logger.info("\n\n");
        logger.info("=================================Getting One-Time Buying Customers=========================");

        Optional<List<Customer>> oneTimeBuyingCustomers = paymentRepository.getOneTimeBuyingCustomers(tillNumber);

        List<Customer> customerList = new ArrayList<>();

        if( oneTimeBuyingCustomers.isPresent() )
        {
            customerList = oneTimeBuyingCustomers.get();
            updateCustomersToSegment(oneTimeBuyingCustomers.get(), CustomerSegment.ONE_TIME_BUYER.name());
        }

        logger.info("=================================Finished Getting One-Time Buying Customers=========================");
        logger.info("\n\n");

        return customerList;
    }

    List<Customer> getRecoveringCustomers(String tillNumber)
    {
        logger.info("\n\n");
        logger.info("=================================Getting Recovering Customers=========================");

        String previousSegregationStatus = CustomerSegment.DORMANT_CUSTOMER.name();
        Optional<List<Customer>> recoveringCustomers = paymentRepository.getRecoveringCustomers(tillNumber, previousSegregationStatus);

        List<Customer> customerList = new ArrayList<>();

        if( recoveringCustomers.isPresent() )
        {
            customerList = recoveringCustomers.get();
            updateCustomersToSegment(recoveringCustomers.get(), CustomerSegment.RECOVERING_CUSTOMER .name());
        }

        logger.info("=================================Finished Getting Recovering Customers=========================");
        logger.info("\n\n");

        return customerList;
    }

    List<Customer> getDecliningCustomers(String tillNumber)
    {
        logger.info("\n\n");
        logger.info("=================================Getting Declining Customers=========================");

        Optional<List<Customer>> decliningCustomers = paymentRepository.getDecliningCustomers(tillNumber);

        List<Customer> customerList = new ArrayList<>();

        if( decliningCustomers.isPresent() )
        {
            customerList = decliningCustomers.get();
            updateCustomersToSegment(decliningCustomers.get(), CustomerSegment.DECLINING_CUSTOMER .name());
        }

        logger.info("=================================Finished Getting Declining Customers=========================");
        logger.info("\n\n");

        return customerList;
    }

    List<Customer> getConsistentCustomers(String tillNumber)
    {
        logger.info("\n\n");
        logger.info("=================================Getting Consistent Customers=========================");

        Optional<List<Customer>> consistentCustomers = paymentRepository.getConsistentCustomers(tillNumber);

        List<Customer> customerList = new ArrayList<>();

        if( consistentCustomers.isPresent() )
        {
            customerList = consistentCustomers.get();
            updateCustomersToSegment(consistentCustomers.get(), CustomerSegment.CONSISTENT_CUSTOMER .name());
        }

        logger.info("=================================Finished Getting Consistent Customers=========================");
        logger.info("\n\n");

        return customerList;
    }
}
