package com.dayworks_ltd.loyalty_engine.inventory.repositories;

import com.dayworks_ltd.loyalty_engine.inventory.models.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findByMerchantIdAndRecordDateBetween(
            String merchantId, LocalDate startDate, LocalDate endDate);

    List<Inventory> findByMerchantIdAndRecordDateGreaterThanEqual(
            String merchantId, LocalDate startDate);

    List<Inventory> findByMerchantId(String merchantId);

    Optional<Inventory> findByMerchantIdAndItemNameAndRecordDate(String merchantId, String itemName, LocalDate recordDate);

    List<Inventory> findByMerchantIdAndClosingStockLessThan(String merchantId, int threshold);

    Optional<Inventory> findByMerchantIdAndItemCode(String merchantId, String itemCode);

    List<Inventory> findByMerchantIdAndRecordDate(String merchantId, LocalDate date);

    Optional<Inventory> findFirstByMerchantIdAndRecordDate(String merchantId, LocalDate date);


    Optional<Inventory> findByMerchantIdAndItemCodeAndRecordDate(
            String merchantId, String itemCode, LocalDate date);

    Optional<Inventory> findByIdAndMerchantId(Long id, String merchantId);

    Optional<Inventory> findByMerchantIdAndItemName(String merchantId, String itemName);



    List<Inventory> findByMerchantIdAndIsActive(String merchantId, Boolean isActive);

    boolean existsByMerchantIdAndItemName(String merchantId, String itemName);

    // Optional: case-insensitive version (very useful for names)
    @Query("SELECT COUNT(i) > 0 FROM Inventory i " +
            "WHERE i.merchantId = :merchantId AND LOWER(i.itemName) = LOWER(:itemName)")
    boolean existsByMerchantIdAndItemNameIgnoreCase(
            @Param("merchantId") String merchantId,
            @Param("itemName") String itemName);

}
