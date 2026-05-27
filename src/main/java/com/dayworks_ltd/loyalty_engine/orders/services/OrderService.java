package com.dayworks_ltd.loyalty_engine.orders.services;



import com.dayworks_ltd.loyalty_engine.common.OrderStatus;
import com.dayworks_ltd.loyalty_engine.orders.dto.OrderItemRequest;
import com.dayworks_ltd.loyalty_engine.orders.dto.OrderRequest;
import com.dayworks_ltd.loyalty_engine.orders.models.Order;
import com.dayworks_ltd.loyalty_engine.orders.models.OrderItem;
import com.dayworks_ltd.loyalty_engine.orders.repositories.OrderRepository;
import com.dayworks_ltd.loyalty_engine.merchants.Merchant;
import com.dayworks_ltd.loyalty_engine.merchants.MerchantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final MerchantRepository merchantRepository;
    private final RestTemplate restTemplate;

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
        if (calculatedTotal.compareTo(request.getAmount()) != 0) {
            throw new IllegalArgumentException("Provided amount does not match items total");
        }

        // Create Order
        Order order = Order.builder()
                .orderCode(orderCode)
                .merchant(merchant)
                .distributor(distributor)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .totalAmount(request.getAmount())
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
                    "amount", request.getAmount()
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
        return "ORD-" + LocalDateTime.now().getYear() +
                String.format("%02d", LocalDateTime.now().getMonthValue()) +
                "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
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
}