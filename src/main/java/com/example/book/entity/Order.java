package com.example.book.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_phone", nullable = false)
    private String customerPhone;

    @Column(name = "customer_address", nullable = false)
    private String customerAddress;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "order_status", nullable = false)
    private String orderStatus; // PENDING, PAID, FAILED, CANCELLED

    // ⭐ NEW: COD / VNPAY
    @Column(name = "payment_method")
    private String paymentMethod;

    // ⭐ NEW: dùng cho VNPay callback
    @Column(name = "txn_ref", unique = true)
    private String txnRef;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}