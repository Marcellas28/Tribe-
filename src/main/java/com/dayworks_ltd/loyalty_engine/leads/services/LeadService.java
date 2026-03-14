package com.dayworks_ltd.loyalty_engine.leads.services;

import com.dayworks_ltd.loyalty_engine.common.LeadResult;
import com.dayworks_ltd.loyalty_engine.customers.Customer;
import com.dayworks_ltd.loyalty_engine.customers.CustomerRepo;
import com.dayworks_ltd.loyalty_engine.dto.LeadDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeadService {

    private final CustomerRepo customerRepository;

    /**
     * Get leads created between two dates
     */
    public List<Customer> getLeadsBetween(LocalDateTime start, LocalDateTime end) {
        return customerRepository.findAllByIsLeadIsTrueAndCreatedAtBetween(start, end);
    }

    /**
     * Create a new lead or update an existing one based on phone number
     */
    @Transactional
    public LeadResult createOrUpdateLead(LeadDto dto) {
        return customerRepository.findByPhoneNumber(dto.getPhoneNumber())
                .map(existing -> {
                    if (!existing.isLead()) {
                        throw new IllegalStateException("This is an existing customer");
                    }
                    existing.setName(dto.getName());
                    existing.setGender(dto.getGender());
                    existing.setCustomerType(dto.getCustomerType());
                    existing.setLocality(dto.getLocality());
                    Customer updated = customerRepository.save(existing);
                    return new LeadResult(updated, false);
                })
                .orElseGet(() -> {
                    Customer newLead = Customer.builder()
                            .name(dto.getName())
                            .phoneNumber(dto.getPhoneNumber())
                            .gender(dto.getGender())
                            .locality(dto.getLocality())
                            .customerType(dto.getCustomerType())
                            .isLead(true)
                            .totalLitres(0.0)
                            .createdAt(LocalDateTime.now())
                            .build();
                    Customer saved = customerRepository.save(newLead);
                    return new LeadResult(saved, true);
                });
    }

}
