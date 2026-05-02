package com.flashdrop.orderService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private String orderId;
    private String status;
    private String message;
    private Long remainingStock;  // only for flash sale orders

    // Static factory methods for clean code
    public static OrderResponse confirmed(String orderId, Long remaining) {
        return OrderResponse.builder()
                .orderId(orderId)
                .status("CONFIRMED")
                .message("Order placed successfully")
                .remainingStock(remaining)
                .build();
    }

    public static OrderResponse cancelled(String orderId) {
        return OrderResponse.builder()
                .orderId(orderId)
                .status("CANCELLED")
                .message("Order cancelled - insufficient stock")
                .build();
    }

    public static OrderResponse normal(String orderId) {
        return OrderResponse.builder()
                .orderId(orderId)
                .status("NORMAL")
                .message("Order routed to normal inventory")
                .build();
    }
}