package com.dayworks_ltd.loyalty_engine.campaigns;
import com.dayworks_ltd.loyalty_engine.common.ApiResponseBody;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/api/v1/campaigns")
public class CampaignController {

    private final CampaignService campaignService;

    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    //
//    @PostMapping
//    public ResponseEntity<CampaignEntity> createCampaign(@RequestBody CampaignEntity campaign) {
//        CampaignEntity saved = campaignService.createCampaign(campaign);
//        return ResponseEntity.ok(saved);
//    }

    @PostMapping("/createCampaign")
    public ResponseEntity<ApiResponseBody> createCampaign(@Valid @RequestBody CampaignEntity campaign) {
        log.info("Received request to create campaign: {}", campaign.getCampaignName());
        System.out.println("Received request to create campaign: " + campaign.getCampaignName());
        try {
            CampaignEntity saved = campaignService.createCampaign(campaign);
            log.info("Campaign created successfully with ID: {}", saved.getId());
            System.out.println("Campaign created successfully with ID: " + saved.getId());

            ApiResponseBody response = ApiResponseBody.builder()
                    .status("200")
                    .message("success")
                    .respObject(saved)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Failed to create campaign: {}", e.getMessage());
            System.out.println("Failed to create campaign: " + e.getMessage());

            String status = e.getClass().getSimpleName().equals("ConstraintViolationException") ? "400" : "500";
            ApiResponseBody response = ApiResponseBody.builder()
                    .status(status)
                    .message(e.getMessage() != null ? e.getMessage() : "Failed to create campaign")
                    .respObject(null)
                    .build();
            return new ResponseEntity<>(response, status.equals("400") ? HttpStatus.BAD_REQUEST : HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get All Campaigns
    @GetMapping
    public ResponseEntity<List<CampaignEntity>> getAllCampaigns(@RequestParam(required = true) Long merchantId) {
//        return ResponseEntity.ok(campaignService.getAllCampaigns());
        return ResponseEntity.ok(campaignService.getAllCampaignsForMerchant(merchantId));
    }

    //  Get Campaign by ID
    @GetMapping("/{id}")
    public ResponseEntity<CampaignEntity> getCampaignById(@PathVariable Long id) {
        return campaignService.getCampaignById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //  Update Campaign
    @PutMapping("/{id}")
    public ResponseEntity<CampaignEntity> updateCampaign(
            @PathVariable Long id,
            @RequestBody CampaignEntity campaign
    ) {
        return ResponseEntity.ok(campaignService.updateCampaign(id, campaign));
    }

    //  Delete Campaign
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCampaign(@PathVariable Long id) {
        campaignService.deleteCampaign(id);
        return ResponseEntity.noContent().build();
    }
}
