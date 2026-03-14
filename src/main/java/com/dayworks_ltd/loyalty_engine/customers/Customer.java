package com.dayworks_ltd.loyalty_engine.customers;

import com.dayworks_ltd.loyalty_engine.campaign.model.CampaignMessageSent;
import com.dayworks_ltd.loyalty_engine.common.customersType;
import com.dayworks_ltd.loyalty_engine.merchants.Merchant;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private String name;

    @Column(name = "total_litres", nullable = false)
    private Double totalLitres;

//    @Column(name = "total_points", nullable = false)
//    private Integer totalPoints;

    @Column(name = "last_transaction")
    private LocalDateTime lastTransaction;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany( mappedBy = "customer" )
    private List<CampaignMessageSent> messagesReceived;

    @ManyToOne
    @JoinColumn(name = "preferred_merchant_id")
    private Merchant preferredMerchant;

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_type", nullable = false)
    private customersType customerType;

    @Column(name = "is_lead", nullable = false)
    private boolean isLead;

    @Column
    private String gender;

    @Column
    private String locality;

    @Column                    //Id of user that collected this customer as a lead.
    private String collectedBy;//NULL if collected from payment transaction
    @Column(name = "total_amount_spent", nullable = false)
    private Integer totalAmountSpent = 0;

    @Column(name = "total_transactions", nullable = false)
    private Integer totalTransactions = 0;

    @Column(name = "last_payment_date")
    private LocalDateTime lastPaymentDate;

    @Column(name = "average_transaction_value")
    private Double averageTransactionValue = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_tier")
    private CustomerTier customerTier = CustomerTier.BRONZE;

    @Column(name = "customer_segment")
    private String customerSegment;

    // Existing fields


    // Method to update customer stats when payment is made
    public void updateSpendingStats(Integer paymentAmount) {
        this.totalAmountSpent += paymentAmount;
        this.totalTransactions += 1;
        this.lastPaymentDate = LocalDateTime.now();
        this.averageTransactionValue = (double) this.totalAmountSpent / this.totalTransactions;

        // Update tier based on total spending
        updateCustomerTier();
    }

    private void updateCustomerTier() {
        if (this.totalAmountSpent >= 50000) {
            this.customerTier = CustomerTier.PLATINUM;
            this.customerSegment = "VIP";
        } else if (this.totalAmountSpent >= 20000) {
            this.customerTier = CustomerTier.GOLD;
            this.customerSegment = "Premium";
        } else if (this.totalAmountSpent >= 5000) {
            this.customerTier = CustomerTier.SILVER;
            this.customerSegment = "Regular";
        } else {
            this.customerTier = CustomerTier.BRONZE;
            this.customerSegment = "New";
        }
    }

    public enum CustomerTier {
        BRONZE, SILVER, GOLD, PLATINUM
    }

}
