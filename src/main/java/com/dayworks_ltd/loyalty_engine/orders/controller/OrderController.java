package com.dayworks_ltd.loyalty_engine.orders.controller;


import com.dayworks_ltd.loyalty_engine.auth.model.CustomUserDetails;
import com.dayworks_ltd.loyalty_engine.auth.model.User;
import com.dayworks_ltd.loyalty_engine.auth.repository.UserRepository;
import com.dayworks_ltd.loyalty_engine.inventory.models.StockTransfer;
import com.dayworks_ltd.loyalty_engine.orders.dto.OrderRequest;
import com.dayworks_ltd.loyalty_engine.orders.models.Order;
import com.dayworks_ltd.loyalty_engine.orders.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    /**
     * Merchant creates a new order + initiates M-Pesa STK Push
     */
    @PostMapping("/create")
    @Operation(summary = "Create Order and Initiate Payment")
    public ResponseEntity<?> createOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody OrderRequest request) {

        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of(
                    "status", "ERROR",
                    "message", "Unauthorized"
            ));
        }

        try {
            // === RESOLVE REAL MERCHANT ID (Same pattern as your daily-summary) ===
            Long userId = userDetails.getUserId();
            Optional<User> userOpt = userRepository.findById(userId);

            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "FAILURE",
                        "statusCode", 400,
                        "message", "User not found"
                ));
            }

            User user = userOpt.get();
            String merchantId = user.getMerchantId();

            if (merchantId == null || merchantId.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "FAILURE",
                        "statusCode", 400,
                        "message", "This user is not linked to any merchant"
                ));
            }

            // Create Order + Initiate Payment
            Order order = orderService.createOrder(merchantId, request);

            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "message", "Order created successfully. M-Pesa prompt sent to your phone.",
                    "orderCode", order.getOrderCode(),
                    "merchantId", merchantId,
                    "totalAmount", order.getTotalAmount()
            ));

        } catch (IllegalArgumentException e) {
            logger.warn("Validation error in order creation: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "FAILURE",
                    "statusCode", 400,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            logger.error("Error creating order", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "ERROR",
                    "statusCode", 500,
                    "message", "Failed to create order"
            ));
        }
    }

    /**
     * Check payment status of an order
     */
    @GetMapping("/{orderCode}/payment-status")
    @Operation(summary = "Check M-Pesa Payment Status")
    public ResponseEntity<?> checkPaymentStatus(@PathVariable String orderCode) {

        try {
            Map<String, Object> result = orderService.checkPaymentStatus(orderCode);

            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "orderCode", orderCode,
                    "data", result
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "ERROR",
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * Get all orders for the logged-in merchant
     */
    @GetMapping("/my-orders")
    @Operation(summary = "Get my orders")
    public ResponseEntity<?> getMyOrders(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("status", "ERROR", "message", "Unauthorized"));
        }

        try {
            Long userId = userDetails.getUserId();
            Optional<User> userOpt = userRepository.findById(userId);

            if (userOpt.isEmpty() || userOpt.get().getMerchantId() == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "FAILURE",
                        "message", "Merchant not found"
                ));
            }

            String merchantId = userOpt.get().getMerchantId();
            List<Order> orders = orderService.getOrdersByMerchant(merchantId);

            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "count", orders.size(),
                    "data", orders
            ));
        } catch (Exception e) {
            logger.error("Error fetching my orders", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "ERROR",
                    "message", "Failed to fetch orders"
            ));
        }
    }

    /**
     * Get pending orders for distributor (for fulfillment)
     */
    @GetMapping("/distributor/pending")
    @Operation(summary = "Get pending paid orders for distributor")
    public ResponseEntity<?> getPendingOrdersForDistributor(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("status", "ERROR", "message", "Unauthorized"));
        }

        try {
            Long userId = userDetails.getUserId();
            Optional<User> userOpt = userRepository.findById(userId);

            if (userOpt.isEmpty() || userOpt.get().getMerchantId() == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "FAILURE",
                        "message", "Merchant not found"
                ));
            }

            String distributorId = userOpt.get().getMerchantId();
            List<Order> orders = orderService.getPendingOrdersForDistributor(distributorId);

            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "count", orders.size(),
                    "data", orders
            ));
        } catch (Exception e) {
            logger.error("Error fetching pending orders", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "ERROR",
                    "message", "Failed to fetch pending orders"
            ));
        }
    }

    @PostMapping("/{orderCode}/fulfill")
    @Operation(summary = "Distributor fulfills a paid order")
    public ResponseEntity<?> fulfillOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String orderCode) {

        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("status", "ERROR", "message", "Unauthorized"));
        }

        try {
            Long userId = userDetails.getUserId();
            User user = userRepository.getUserById(userId);
            String distributorId = user.getMerchantId();

            StockTransfer transfer = orderService.fulfillOrder(orderCode, distributorId, userId);

            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "message", "Order fulfilled successfully",
                    "orderCode", orderCode,
                    "transferCode", transfer.getTransferCode()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "FAILURE",
                    "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/{orderCode}/receive")
    @Operation(summary = "Merchant confirms stock receipt by entering order code from receipt")
    public ResponseEntity<?> receiveOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String orderCode) {

        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of(
                    "status", "ERROR",
                    "message", "Unauthorized"
            ));
        }

        try {
            Long userId = userDetails.getUserId();
            Optional<User> userOpt = userRepository.findById(userId);

            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "FAILURE",
                        "message", "User not found"
                ));
            }

            User user = userOpt.get();
            String merchantId = user.getMerchantId();

            if (merchantId == null || merchantId.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "FAILURE",
                        "message", "User is not linked to any merchant"
                ));
            }

            Order order = orderService.receiveOrder(orderCode, merchantId);

            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "message", "Stock received successfully",
                    "orderCode", orderCode,
                    "receivedDate", order.getReceivedDate().toString()
            ));

        } catch (IllegalArgumentException e) {
            logger.warn("Receive order failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "FAILURE",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            logger.error("Error receiving order", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "ERROR",
                    "message", "Failed to receive order"
            ));
        }
    }
}