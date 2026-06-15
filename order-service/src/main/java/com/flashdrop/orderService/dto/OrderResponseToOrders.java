package com.flashdrop.orderService.dto;

import com.flashdrop.orderService.entity.Order;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseToOrders {
    private String orderId;
    private String productId;
    private String email;
    private int quantity;
    private double totalPrice;
    private LocalDateTime orderDate;
    private String status;
    private String imageUrl;
    private String productDescription;
}
