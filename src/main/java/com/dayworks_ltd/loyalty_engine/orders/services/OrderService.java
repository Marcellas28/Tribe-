package com.dayworks_ltd.loyalty_engine.orders.services;
import com.dayworks_ltd.loyalty_engine.auth.enums.TransferType;
import com.dayworks_ltd.loyalty_engine.common.OrderStatus;
import com.dayworks_ltd.loyalty_engine.inventory.DTO.StockTransferItemRequest;
import com.dayworks_ltd.loyalty_engine.inventory.DTO.StockTransferRequest;
import com.dayworks_ltd.loyalty_engine.inventory.models.Inventory;
import com.dayworks_ltd.loyalty_engine.inventory.services.ProductPerformanceService;
import com.dayworks_ltd.loyalty_engine.inventory.services.StockTransferService;
import com.dayworks_ltd.loyalty_engine.inventory.models.StockTransfer;
import com.dayworks_ltd.loyalty_engine.orders.dto.OrderItemRequest;
import com.dayworks_ltd.loyalty_engine.orders.dto.OrderRequest;
import com.dayworks_ltd.loyalty_engine.orders.models.Order;
import com.dayworks_ltd.loyalty_engine.orders.models.OrderItem;
import com.dayworks_ltd.loyalty_engine.orders.repositories.OrderRepository;
import com.dayworks_ltd.loyalty_engine.merchants.Merchant;
import com.dayworks_ltd.loyalty_engine.merchants.MerchantRepository;
import com.dayworks_ltd.loyalty_engine.inventory.repositories.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final MerchantRepository merchantRepository;
    private final InventoryRepository inventoryRepository;
    private final RestTemplate restTemplate;
    private final StockTransferService stockTransferService;
    private final ProductPerformanceService productPerformanceService; // add this


    @Value("${payment.base-url}")
    private String paymentBaseUrl;

    /**
     * Create Order + Initiate M-Pesa STK Push
     */
    @Transactional
    public Order createOrder(String merchantId, OrderRequest request) {

        Merchant distributor = merchantRepository.findById(request.getDistributorId())
                .orElseThrow(() -> new IllegalArgumentException("Distributor not found"));

        Merchant merchant = merchantRepository.findById(Long.parseLong(merchantId))
                .orElseThrow(() -> new IllegalArgumentException("Merchant not found"));

        String orderCode = generateOrderCode();

        BigDecimal calculatedTotal = calculateTotal(request.getItems());


        // Create Order
        Order order = Order.builder()
                .orderCode(orderCode)
                .merchant(merchant)
                .distributor(distributor)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .totalAmount(calculatedTotal)
                .phoneNumber(request.getPhoneNumber())                .build();

        for (OrderItemRequest itemReq : request.getItems()) {
            OrderItem item = OrderItem.builder()
                    .order(order)
                    .itemCode(itemReq.getItemCode())
                    .itemName(itemReq.getItemName())
                    .quantity(itemReq.getQuantity())
                    .wholesalePrice(itemReq.getWholesalePrice())
                    .lineTotal(itemReq.getWholesalePrice().multiply(BigDecimal.valueOf(itemReq.getQuantity())))
                    .build();

            order.addItem(item);
        }
        Order savedOrder = orderRepository.save(order);

        // === INITIATE STK PUSH ===
        try {
            Map<String, Object> stkRequest = Map.of(
                    "phoneNumber", request.getPhoneNumber(),
                    "amount", calculatedTotal
            );

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    paymentBaseUrl + "/api/v1/payment/initiate-stk-push",
                    stkRequest,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
                String checkoutRequestId = (String) data.get("CheckoutRequestID");

                savedOrder.setCheckoutRequestId(checkoutRequestId);
                orderRepository.save(savedOrder);

                log.info("STK Push initiated for Order {} | CheckoutRequestID: {}", orderCode, checkoutRequestId);
            }
        } catch (Exception e) {
            log.error("Failed to initiate STK Push for order {}", orderCode, e);
        }

        return savedOrder;
    }

    /**
     * Check Payment Status
     */
    @Transactional
    public Map<String, Object> checkPaymentStatus(String orderCode) {

        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getCheckoutRequestId() == null) {
            throw new IllegalArgumentException("No payment initiated for this order");
        }

        try {
            Map<String, Object> confirmRequest = Map.of(
                    "originatorConversationId", order.getCheckoutRequestId()
            );

            log.info("Checking payment status for Order: {} | CheckoutRequestID: {}",
                    orderCode, order.getCheckoutRequestId());

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    paymentBaseUrl + "/api/v1/payment/confirm-payment",
                    confirmRequest,
                    Map.class
            );

            // === DETAILED SAFARICOM RESPONSE LOGGING ===
            log.info("Safaricom Response for Order {}: {}", orderCode, response.getBody());

            if (response.getBody() != null) {
                log.info("Safaricom Raw Response: {}", response.getBody());

                Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
                if (data != null) {
                    log.info("Safaricom ResponseCode: {}", data.get("ResponseCode"));
                    log.info("Safaricom ResponseDescription: {}", data.get("ResponseDescription"));
                    log.info("Safaricom ConversationID: {}", data.get("ConversationID"));
                }
            }

            boolean isPaid = isPaymentSuccessful(response.getBody());

            if (isPaid) {
                order.setStatus(OrderStatus.PAID);
                order.setPaymentDate(LocalDateTime.now());
                order.setPaymentReference(order.getCheckoutRequestId());
                orderRepository.save(order);

                log.info("✅ PAYMENT SUCCESSFUL - Order {} marked as PAID", orderCode);
            } else {
                log.warn("❌ Payment NOT confirmed yet for Order: {}", orderCode);
            }

            return Map.of(
                    "orderCode", orderCode,
                    "orderStatus", order.getStatus().name(),
                    "isPaid", isPaid,
                    "paymentResponse", response.getBody()
            );

        } catch (Exception e) {
            log.error("❌ Error checking payment status for order {}", orderCode, e);
            throw new RuntimeException("Failed to check payment status");
        }
    }

    // ==================== Other Methods ====================
    public List<Order> getOrdersByMerchant(String merchantId) {
        return orderRepository.findByMerchantIdOrderByOrderDateDesc(Long.parseLong(merchantId));
    }

    public List<Order> getPendingOrdersForDistributor(String distributorId) {
        return orderRepository.findByDistributorIdAndStatus(
                Long.parseLong(distributorId), OrderStatus.PAID);
    }

//    public List<Order> getPendingOrdersForDistributor(String distributorId) {
//        return orderRepository.findByDistributorIdAndStatus(distributorId, OrderStatus.PAID);
//    }

    // ====================== Helpers ======================

    private String generateOrderCode() {
        int code = (int)(Math.random() * 900000) + 100000;
        return String.valueOf(code);
    }

    private BigDecimal calculateTotal(List<OrderItemRequest> items) {
        return items.stream()
                .map(item -> item.getWholesalePrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean isPaymentSuccessful(Map responseBody) {
        try {
            if (responseBody == null || !responseBody.containsKey("data")) return false;
            Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
            Object responseCode = data.get("ResponseCode");
            return responseCode != null && ("0".equals(responseCode.toString()) || 0 == Double.parseDouble(responseCode.toString()));
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public StockTransfer fulfillOrder(String orderCode, String distributorMerchantId, Long issuedByUserId) {

        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        switch (order.getStatus()) {
            case PENDING -> throw new IllegalArgumentException("Order has not been paid yet");
            case FULFILLED -> throw new IllegalArgumentException(
                    "Order already fulfilled. Transfer: " + order.getStockTransfer().getTransferCode()
            );
            case RECEIVED -> throw new IllegalArgumentException("Order already received by merchant");
            case CANCELLED -> throw new IllegalArgumentException("Order has been cancelled");
            case PAID -> {} // valid — proceed
        }


        if (!order.getDistributor().getId().toString().equals(distributorMerchantId)) {
            throw new IllegalArgumentException("You are not the distributor for this order");
        }



        validateDistributorStock(order, distributorMerchantId);

        // Create Stock Transfer from Order
        StockTransferRequest transferRequest = createTransferRequestFromOrder(order);

        StockTransfer stockTransfer = stockTransferService.createStockTransfer(transferRequest, issuedByUserId);

        // Link them
        order.markAsFulfilled(stockTransfer);
        orderRepository.save(order);

        log.info("Order {} fulfilled with Stock Transfer {}", orderCode, stockTransfer.getTransferCode());

        return stockTransfer;
    }


    private void validateDistributorStock(Order order, String distributorMerchantId) {
        List<String> insufficientItems = new ArrayList<>();

        for (OrderItem item : order.getItems()) {
            Optional<Inventory> stockOpt = inventoryRepository
                    .findByMerchantIdAndItemCode(distributorMerchantId, item.getItemCode());

            if (stockOpt.isEmpty()) {
                insufficientItems.add(item.getItemName() + " (" + item.getItemCode() + ") - not found in distributor stock");
                continue;
            }

            Inventory stock = stockOpt.get();

            if (stock.getAvailableStock() < item.getQuantity()) {
                insufficientItems.add(
                        item.getItemName() + " (" + item.getItemCode() + ")" +
                                " - Required: " + item.getQuantity() +
                                ", Available: " + stock.getAvailableStock()
                );
            }
        }

        if (!insufficientItems.isEmpty()) {
            throw new IllegalArgumentException(
                    "Insufficient distributor stock for: " + String.join(" | ", insufficientItems)
            );
        }
    }


    private StockTransferRequest createTransferRequestFromOrder(Order order) {
        List<StockTransferItemRequest> items = order.getItems().stream()
                .map(item -> StockTransferItemRequest.builder()
                        .itemCode(item.getItemCode())
                        .itemName(item.getItemName())
                        .quantity(item.getQuantity())
                        .wholesaleUnitPrice(item.getWholesalePrice())
                        .build())
                .toList();

        return StockTransferRequest.builder()
                .distributorId(order.getDistributor().getId())
                .recipientId(order.getMerchant().getId())
                .transferType(TransferType.ORDER_FULFILLMENT)
                .items(items)
                .notes("Fulfillment for Order: " + order.getOrderCode())
                .build();
    }
    @Transactional
    public Order receiveOrder(String orderCode, String receivingMerchantId) {

        // 1. Find order
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderCode));

        // 2. Status guard
        switch (order.getStatus()) {
            case PENDING -> throw new IllegalArgumentException("Order has not been paid yet");
            case PAID -> throw new IllegalArgumentException("Order has not been fulfilled yet");
            case RECEIVED -> throw new IllegalArgumentException("Order already received");
            case CANCELLED -> throw new IllegalArgumentException("Order has been cancelled");
            case FULFILLED -> {} // valid — proceed
            default -> throw new IllegalStateException("Unknown order status: " + order.getStatus());
        }

        // 3. Must be the correct merchant
        if (!order.getMerchant().getId().toString().equals(receivingMerchantId)) {
            throw new IllegalArgumentException("You are not the recipient of this order");
        }

        // 4. Process each item
        for (OrderItem item : order.getItems()) {
            String productSuffix = extractProductSuffix(item.getItemCode());
            String receiverItemCode = receivingMerchantId + "-" + productSuffix;

            Optional<Inventory> stockOpt = inventoryRepository
                    .findByMerchantIdAndItemCode(receivingMerchantId, receiverItemCode);

            if (stockOpt.isPresent()) {
                // Item exists — update stock
                Inventory stock = stockOpt.get();
                stock.applyRestock(item.getQuantity(), item.getWholesalePrice());
                inventoryRepository.save(stock);

                // Track restock in performance
                try {
                    productPerformanceService.updateRestock(stock, item.getQuantity());
                } catch (Exception e) {
                    log.warn("Performance restock update failed for item {}: {}",
                            receiverItemCode, e.getMessage());
                }

            } else {
                // Item does not exist — create new inventory record
                Inventory newStock = Inventory.builder()
                        .merchantId(receivingMerchantId)
                        .itemCode(receiverItemCode)
                        .itemName(item.getItemName())
                        .startingStock(0)                 // always zero for new items
                        .addedStock(item.getQuantity())   // received quantity goes here
                        .soldStock(0)
                        .availableStock(item.getQuantity())
                        .closingStock(item.getQuantity())
                        .wholesalePrice(item.getWholesalePrice())
                        .unitCost(item.getWholesalePrice())
                        .unitPrice(null)
                        .reorderLevel(10)
                        .isActive(true)
                        .recordDate(LocalDate.now())
                        .lastUpdated(LocalDateTime.now())
                        .lastRestockDate(LocalDateTime.now())
                        .totalSales(BigDecimal.ZERO)
                        .grossSales(BigDecimal.ZERO)
                        .netlSales(BigDecimal.ZERO)
                        .deductions(BigDecimal.ZERO)
                        .build();

                inventoryRepository.save(newStock);
                log.info("New inventory item created for merchant {} — item: {}",
                        receivingMerchantId, receiverItemCode);
            }
        }

        // 5. Mark order received
        order.markAsReceived();
        orderRepository.save(order);

        log.info("Order {} received by merchant {}", orderCode, receivingMerchantId);
        return order;
    }
    private String extractProductSuffix(String itemCode) {
        int dashIndex = itemCode.indexOf('-');
        return dashIndex >= 0 ? itemCode.substring(dashIndex + 1) : itemCode;
    }
}