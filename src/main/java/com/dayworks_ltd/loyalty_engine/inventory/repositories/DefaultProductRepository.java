package com.dayworks_ltd.loyalty_engine.inventory.repositories;

import com.dayworks_ltd.loyalty_engine.inventory.models.DefaultProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DefaultProductRepository extends JpaRepository<DefaultProduct, Long> {
    boolean existsByProductCode(String productCode);
}
