package com.dayworks_ltd.loyalty_engine.credit_engine.repository;

import com.dayworks_ltd.loyalty_engine.credit_engine.model.CreditScore;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CreditScoreRepository extends JpaRepository<CreditScore, Long> {
    Optional<CreditScore> findByMerchantIdAndCalculatedForDate(String merchantId, LocalDate calculatedForDate);
//    Optional<CreditScore> findByMerchantIdAndCalculatedForDate(String merchantId, LocalDate date);
    void deleteByMerchantIdAndCalculatedForDate(String merchantId, LocalDate date);

    List<CreditScore> findByCalculatedForDate(LocalDate date);
}