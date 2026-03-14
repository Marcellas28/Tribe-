package com.dayworks_ltd.loyalty_engine.leads.controller;

import com.dayworks_ltd.loyalty_engine.common.LeadResult;
import com.dayworks_ltd.loyalty_engine.customers.Customer;
import com.dayworks_ltd.loyalty_engine.dto.LeadDto;
import com.dayworks_ltd.loyalty_engine.leads.services.LeadService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.dayworks_ltd.loyalty_engine.common.ApiResponseBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/leads")
@RequiredArgsConstructor
public class LeadController {

    private final LeadService leadService;


    @GetMapping
    public ResponseEntity<ApiResponseBody> listLeadsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        List<Customer> leads = leadService.getLeadsBetween(
                start.atStartOfDay(),
                end.atTime(23, 59, 59)
        );

        ApiResponseBody response = new ApiResponseBody();
        response.setStatus("SUCCESS");
        response.setMessage("Leads retrieved successfully");
        response.setRespObject(leads);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/add")
    public ApiResponseBody createOrUpdateLead(@RequestBody LeadDto dto) {
        System.out.println("dto = " + dto.getPhoneNumber());

        LeadResult result = leadService.createOrUpdateLead(dto);
        ApiResponseBody response = new ApiResponseBody();
        response.setStatus("SUCCESS");
        response.setRespObject(result.getCustomer());
        if (result.isNew()) {
            response.setMessage("Lead created successfully");
        } else {
            response.setMessage("Lead updated successfully");
        }
        return response;
    }


}
