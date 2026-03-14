package com.dayworks_ltd.loyalty_engine.merchants;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MerchantRepository extends JpaRepository<Merchant, Long>{
    Optional<Merchant> findByTillNumber(String tillNumber);
    boolean existsByTillNumber(String tillNumber);

}
