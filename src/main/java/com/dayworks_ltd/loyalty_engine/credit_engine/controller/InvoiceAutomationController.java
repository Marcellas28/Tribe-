package com.dayworks_ltd.loyalty_engine.credit_engine.controller;

import com.dayworks_ltd.loyalty_engine.auth.model.User;
import com.dayworks_ltd.loyalty_engine.auth.repository.UserRepository;
import com.dayworks_ltd.loyalty_engine.credit_engine.dto.InvoiceExtractionResponse;
import com.dayworks_ltd.loyalty_engine.credit_engine.model.SupplierInvoice;
import com.dayworks_ltd.loyalty_engine.credit_engine.service.InvoiceAutomationService;
import com.dayworks_ltd.loyalty_engine.credit_engine.service.InvoicePersistenceService;
import com.dayworks_ltd.loyalty_engine.credit_engine.service.InvoiceToInventorySyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
@Slf4j
public class InvoiceAutomationController {

    private final InvoiceAutomationService automationService;
    private final InvoiceToInventorySyncService syncService;
    private final InvoicePersistenceService persistenceService; // ← injected here
    private final UserRepository userRepository;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadInvoice(
            @RequestParam("invoice_file") MultipartFile invoiceFile,
            @RequestParam("merchant_id") String merchantId) {

        Map<String, Object> response = new HashMap<>();

        try {
            // ── 1. RESOLVE USER → MERCHANT ────────────────────────────────────
            Long userId = Long.parseLong(merchantId);

            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "FAILURE",
                        "statusCode", 400,
                        "message", "No user with specified ID exists"
                ));
            }

            User user = userOpt.get();
            String realMerchantId = user.getMerchantId();

            if (realMerchantId == null || realMerchantId.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "FAILURE",
                        "statusCode", 400,
                        "message", "This user is not linked to any merchant"
                ));
            }

            log.info("UPLOAD-INVOICE - userId={} resolved to merchantId={}", userId, realMerchantId);

            // ── 2. SUBMIT TO OCR + POLL FOR RESULT ───────────────────────────
            InvoiceExtractionResponse submission = automationService.submitInvoiceForProcessing(invoiceFile);
            String submissionId = submission.getInvoiceSubmissionId();

            InvoiceExtractionResponse finalResult = pollUntilComplete(submissionId, 12, 5000);

            // ── 3. PERSIST SUPPLIER + INVOICE ────────────────────────────────
            // realMerchantId comes back as String from user.getMerchantId().
            // SupplierInvoice.merchantId is Long — parse once, here, explicitly.
            Long resolvedMerchantId = Long.parseLong(realMerchantId);
            SupplierInvoice savedInvoice = persistenceService.persistInvoice(finalResult, resolvedMerchantId);

            // ── 4. BUILD RESPONSE ─────────────────────────────────────────────
            response.put("success", true);
            response.put("invoiceSubmissionId", submissionId);
            response.put("savedInvoiceId", savedInvoice.getId());
            response.put("supplierId", savedInvoice.getSupplierId());
            response.put("status", "complete");
            response.put("data", finalResult.getData());
            response.put("confidence", finalResult.getData().getConfidence());
            response.put("realMerchantId", realMerchantId);
            response.put("message", "Invoice extracted and saved. Review items to add to inventory.");

            return ResponseEntity.ok(response);

        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "FAILURE",
                    "statusCode", 400,
                    "message", "Invalid merchant_id format — must be a numeric user ID"
            ));

        } catch (InvoicePersistenceService.DuplicateInvoiceException e) {
            // Same invoice uploaded twice — not a server error, tell the client clearly
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "status", "DUPLICATE",
                    "statusCode", 409,
                    "message", e.getMessage(),
                    "invoiceNumber", e.getInvoiceNumber()
            ));

        } catch (TimeoutException e) {
            response.put("success", false);
            response.put("status", "pending");
            response.put("invoiceSubmissionId", e.getSubmissionId());
            response.put("message", "Processing is taking longer than expected. Check status later using the submission ID.");
            return ResponseEntity.accepted().body(response); // 202

        } catch (Exception e) {
            log.error("UPLOAD-INVOICE - Unhandled failure", e);
            response.put("success", false);
            response.put("message", "Processing failed: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // ── Polling helper ────────────────────────────────────────────────────────
    private InvoiceExtractionResponse pollUntilComplete(
            String submissionId, int maxAttempts, long delayMs) throws TimeoutException {

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            InvoiceExtractionResponse result = automationService.getInvoiceExtractionResult(submissionId);

            if ("complete".equalsIgnoreCase(result.getStatus())) {
                return result;
            }
            if ("error".equalsIgnoreCase(result.getStatus())) {
                throw new RuntimeException("Extraction failed: " + result.getError());
            }

            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }

            log.info("POLL - attempt {}/{} submissionId={} status={}", attempt, maxAttempts, submissionId, result.getStatus());
        }

        throw new TimeoutException("Timeout waiting for OCR completion", submissionId);
    }

    private static class TimeoutException extends Exception {
        private final String submissionId;
        public TimeoutException(String msg, String submissionId) {
            super(msg);
            this.submissionId = submissionId;
        }
        public String getSubmissionId() { return submissionId; }
    }
}
























//package com.dayworks_ltd.loyalty_engine.credit_engine.controller;
//import com.dayworks_ltd.loyalty_engine.auth.model.User;
//import com.dayworks_ltd.loyalty_engine.auth.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import com.dayworks_ltd.loyalty_engine.credit_engine.dto.InvoiceExtractionResponse;
//import com.dayworks_ltd.loyalty_engine.credit_engine.service.InvoiceAutomationService;
//import com.dayworks_ltd.loyalty_engine.credit_engine.service.InvoiceToInventorySyncService;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//@RestController
//@RequestMapping("/api/v1/invoices")
//@RequiredArgsConstructor
//@Slf4j
//public class InvoiceAutomationController {
//
//    @Autowired
//    private final InvoiceAutomationService automationService;
//    private final InvoiceToInventorySyncService syncService;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    /**
//     * Upload invoice and automatically sync to inventory
//     * Similar to your campaign sending endpoints
//     */
//    @PostMapping("/upload")
//    public ResponseEntity<Map<String, Object>> uploadInvoice(
//            @RequestParam("invoice_file") MultipartFile invoiceFile,
//            @RequestParam("merchant_id") String merchantId) {  // ← this is USER ID
//
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            Long userId = Long.parseLong(merchantId);
//
//            Optional<User> userOpt = userRepository.findById(userId);
//            if (userOpt.isEmpty()) {
//                return ResponseEntity.badRequest().body(Map.of(
//                        "status", "FAILURE",
//                        "statusCode", 400,
//                        "message", "No user with specified ID exists"
//                ));
//            }
//
//            User user = userOpt.get();
//            String realMerchantId = user.getMerchantId();
//
//            if (realMerchantId == null || realMerchantId.isBlank()) {
//                return ResponseEntity.badRequest().body(Map.of(
//                        "status", "FAILURE",
//                        "statusCode", 400,
//                        "message", "This user is not linked to any merchant"
//                ));
//            }
//
//            log.info("UPLOAD-INVOICE - Incoming userId: {} → Resolved to real merchantId: {}",
//                    merchantId, realMerchantId);
//
//            InvoiceExtractionResponse submission = automationService.submitInvoiceForProcessing(invoiceFile);
//            String submissionId = submission.getInvoiceSubmissionId();
//
//            InvoiceExtractionResponse finalResult = pollUntilComplete(submissionId, 12, 5000); // max 60s
//
//            response.put("success", true);
//            response.put("invoiceSubmissionId", submissionId);
//            response.put("status", "complete");
//            response.put("data", finalResult.getData());
//            response.put("confidence", finalResult.getData().getConfidence());
//            response.put("realMerchantId", realMerchantId); // optional — helpful for frontend
//            response.put("message", "Invoice extracted successfully. Review and approve in the app to add to inventory.");
//
//            return ResponseEntity.ok(response);
//
//        } catch (NumberFormatException e) {
//            return ResponseEntity.badRequest().body(Map.of(
//                    "status", "FAILURE",
//                    "statusCode", 400,
//                    "message", "Invalid merchant_id format (must be numeric user ID)"
//            ));
//
//        } catch (TimeoutException e) {
//            response.put("success", false);
//            response.put("message", "Processing is taking longer than expected. Submission ID: "
//                    + e.getSubmissionId() + ". You can check status later.");
//            response.put("invoiceSubmissionId", e.getSubmissionId());
//            response.put("status", "pending");
//            return ResponseEntity.accepted().body(response); // 202 Accepted
//
//        } catch (Exception e) {
//            log.error("Invoice upload failed", e);
//            response.put("success", false);
//            response.put("message", "Processing failed: " + e.getMessage());
//            return ResponseEntity.status(500).body(response);
//        }
//    }
//    private InvoiceExtractionResponse pollUntilComplete(String submissionId, int maxAttempts, long delayMs) throws TimeoutException {
//        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
//            InvoiceExtractionResponse result = automationService.getInvoiceExtractionResult(submissionId);
//
//            if ("complete".equalsIgnoreCase(result.getStatus())) {
//                return result;
//            }
//
//            if ("error".equalsIgnoreCase(result.getStatus())) {
//                throw new RuntimeException("Extraction failed: " + result.getError());
//            }
//
//            try {
//                Thread.sleep(delayMs); // 5 seconds
//            } catch (InterruptedException ie) {
//                Thread.currentThread().interrupt();
//            }
//
//            log.info("Poll attempt {}/{} for {} - still {}", attempt, maxAttempts, submissionId, result.getStatus());
//        }
//
//        throw new TimeoutException("Timeout waiting for completion", submissionId);
//    }
//
//    // Simple timeout exception class
//    private static class TimeoutException extends Exception {
//        private final String submissionId;
//        public TimeoutException(String msg, String submissionId) {
//            super(msg);
//            this.submissionId = submissionId;
//        }
//        public String getSubmissionId() { return submissionId; }
//    }
//    /**
//     * Extract invoice data only (preview before sync)
//     * Similar to your test/preview endpoints
//     */
//
//
//}
