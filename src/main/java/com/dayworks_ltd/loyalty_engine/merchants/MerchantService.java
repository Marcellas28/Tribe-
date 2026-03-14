package com.dayworks_ltd.loyalty_engine.merchants;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
@Transactional

public class MerchantService {

    private final MerchantRepository merchantRepository;

    public MerchantService(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    public String getMerchantOtp(String merchantId)
    {
        Optional<Merchant> merchantOptional = merchantRepository.findById(Long.parseLong(merchantId));

        return merchantOptional.isPresent() ? merchantOptional.get().getMerchantOtp() : null;
    }

    public void updateMerchantOtp(String merchantId, String newGeneratedOtp)
    {
        Optional<Merchant> merchantOptional = merchantRepository.findById(Long.parseLong(merchantId));

        if(merchantOptional.isPresent())
        {
            Merchant merchant = merchantOptional.get();
            merchant.setMerchantOtp(newGeneratedOtp);
            merchantRepository.save(merchant);
        }
    }

    public Merchant createMerchant(Merchant merchant) {
        if (merchantRepository.existsByTillNumber(merchant.getTillNumber())) {
            throw new IllegalArgumentException("Merchant with till " + merchant.getTillNumber() + " already exists");
        }
        return merchantRepository.save(merchant);
    }

    public List<Merchant> getAllMerchants() {
        return merchantRepository.findAll();
    }

    public Optional<Merchant> getMerchantById(Long id) {
        return merchantRepository.findById(id);
    }

    public Optional<Merchant> getMerchantByTillNumber(String tillNumber) {
        return merchantRepository.findByTillNumber(tillNumber);
    }

    public Merchant updateMerchant(Long id, Merchant updatedMerchant) {
        return merchantRepository.findById(id).map(existing -> {
            existing.setBusinessName(updatedMerchant.getBusinessName());
            existing.setLocation(updatedMerchant.getLocation());
            existing.setBusinessType(updatedMerchant.getBusinessType());
            existing.setTillNumber(updatedMerchant.getTillNumber());
            return merchantRepository.save(existing);
        }).orElseThrow(() -> new IllegalArgumentException("Merchant not found"));
    }

    public void deleteMerchant(Long id) {
        merchantRepository.deleteById(id);
    }

}
