
package com.dayworks_ltd.loyalty_engine.smm.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auto_replies", indexes = {
        @Index(name = "idx_merchant_id", columnList = "merchant_id"),
        @Index(name = "idx_lead_id", columnList = "lead_id"),
        @Index(name = "idx_platform", columnList = "platform"),
        @Index(name = "idx_sent_at", columnList = "sent_at"),
        @Index(name = "idx_status", columnList = "status")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoReply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "merchant_id", nullable = false)
    private Long merchantId;

    // Link to the lead (optional if reply is from comment, not DM)
    @Column(name = "lead_id")
    private Long leadId;

    // Original customer message that triggered this reply
    @Column(name = "incoming_message", columnDefinition = "TEXT")
    private String incomingMessage;

    // The reply we sent
    @Column(name = "reply_text", columnDefinition = "TEXT")
    private String replyText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Platform platform;                    // FACEBOOK, INSTAGRAM, TIKTOK

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReplyType replyType;                  // STOCK_CHECK, PRICE_INQUIRY, GREETING, SIZE_INQUIRY, CUSTOM

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReplyStatus status = ReplyStatus.SENT; // PENDING, SENT, FAILED, DELIVERED, READ

    // If reply included product info
    @Column(name = "item_name", length = 255)
    private String itemName;

    @Column(name = "item_code", length = 100)
    private String itemCode;

    @Column(name = "stock_level")
    private Integer stockLevel;

    @Column(name = "unit_price", precision = 12, scale = 2)
    private java.math.BigDecimal unitPrice;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    // AI details
    @Column(name = "ai_model", length = 50)
    private String aiModel;                       // e.g. gpt-4o-mini

    @Column(name = "ai_prompt_used", columnDefinition = "TEXT")
    private String aiPromptUsed;

    @Column(name = "response_time_ms")
    private Long responseTimeMs;

    // Platform message ID (for debugging/dedup)
    @Column(name = "platform_message_id", length = 100)
    private String platformMessageId;

    // Timestamps
    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.sentAt == null) {
            this.sentAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Enums
    public enum Platform {
        FACEBOOK, INSTAGRAM, TIKTOK
    }

    public enum ReplyType {
        STOCK_CHECK, PRICE_INQUIRY, SIZE_INQUIRY, COLOR_INQUIRY,
        DELIVERY_INQUIRY, GREETING, PROMO, CUSTOM, FALLBACK
    }

    public enum ReplyStatus {
        PENDING, SENT, FAILED, DELIVERED, READ
    }
}