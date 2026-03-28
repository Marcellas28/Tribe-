package com.dayworks_ltd.loyalty_engine.credit_engine.repository;



import com.dayworks_ltd.loyalty_engine.credit_engine.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    /**
     * Find by KRA PIN — most reliable deduplication key.
     * PIN is unique per legal entity in Kenya.
     */
    Optional<Supplier> findByPin(String pin);

    /**
     * Fallback: find by normalized name when PIN is absent.
     */
    Optional<Supplier> findByNormalizedName(String normalizedName);
}