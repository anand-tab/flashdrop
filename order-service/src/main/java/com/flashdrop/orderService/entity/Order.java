package com.flashdrop.orderService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "customer_orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String orderId;
    private String productId;
    private String email;
    private int quantity;
    private double totalPrice;

    @CreationTimestamp
    private LocalDateTime orderDate;



    @Enumerated(EnumType.STRING)
    private OrderStatus status;  // ✅ new field

    public enum OrderStatus {
        CONFIRMED,
        CANCELLED,
        NORMAL  // non flash sale order
    }
}
