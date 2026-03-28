package com.dayworks_ltd.loyalty_engine.credit_engine.repository;

import com.dayworks_ltd.loyalty_engine.credit_engine.model.SupplierInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierInvoiceRepository extends JpaRepository<SupplierInvoice, Long> {

    /**
     * Duplicate detection via SHA256 hash.
     * Same invoice number + supplier + total + date = same document.
     */
    Optional<SupplierInvoice> findByInvoiceHash(String invoiceHash);

    /**
     * Check if a submission ID was already processed (idempotency guard).
     */
    boolean existsBySubmissionId(String submissionId);
}