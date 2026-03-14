package com.dayworks_ltd.loyalty_engine.campaigns;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CampaignService {

    private final CampaignRepository campaignRepository;

    public CampaignService(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }

    public CampaignEntity createCampaign(CampaignEntity campaign) {
        return campaignRepository.save(campaign);
    }

    public List<CampaignEntity> getAllCampaigns() {
        return campaignRepository.findAll();
    }

    /**
     * Revisit: add merchant ID to campaigns table.
     * **/
    public List<CampaignEntity> getAllCampaignsForMerchant(Long merchantId) {
        return campaignRepository.findAllById(List.of(merchantId));
    }

    public Optional<CampaignEntity> getCampaignById(Long id) {
        return campaignRepository.findById(id);
    }

    public CampaignEntity updateCampaign(Long id, CampaignEntity updated) {
        return campaignRepository.findById(id).map(existing -> {
            existing.setCampaignName(updated.getCampaignName());
            existing.setCampaignType(updated.getCampaignType());
            existing.setTargetAudience(updated.getTargetAudience());
            existing.setStartDate(updated.getStartDate());
            existing.setEndDate(updated.getEndDate());
            return campaignRepository.save(existing);
        }).orElseThrow(() -> new IllegalArgumentException("Campaign not found"));
    }

    public void deleteCampaign(Long id) {
        campaignRepository.deleteById(id);
    }
}
