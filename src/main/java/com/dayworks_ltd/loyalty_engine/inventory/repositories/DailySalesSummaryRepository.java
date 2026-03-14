package com.dayworks_ltd.loyalty_engine.inventory.repositories;

import com.dayworks_ltd.loyalty_engine.inventory.models.DailySalesSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Repository

public interface DailySalesSummaryRepository extends JpaRepository<DailySalesSummary, Long> {
    Optional<DailySalesSummary> findByMerchantIdAndRecordDate(String merchantId, LocalDate date);
    List<DailySalesSummary> findByMerchantIdAndRecordDateBetween(String merchantId, LocalDate startDate, LocalDate endDate);
}