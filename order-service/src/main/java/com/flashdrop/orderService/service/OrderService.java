package com.flashdrop.orderService.service;

import com.flashdrop.orderService.dto.OrderRequest;
import com.flashdrop.orderService.dto.OrderResponse;
import com.flashdrop.orderService.entity.Order;
import com.flashdrop.orderService.redis.InventoryResult;
import com.flashdrop.orderService.redis.RedisInventoryService;
import com.flashdrop.orderService.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderService {


    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private RedisInventoryService redisInventoryService;

    public OrderResponse createOrder(OrderRequest request) {
        log.info("Recievec order request for productId : {}", request.getProductId());

        InventoryResult result = redisInventoryService.checkAndDeduct(request.getProductId(), request.getQuantity());

        log.info("Recievec order request for productId : {}", request.getProductId());


        return switch (result.getStatus()){
            case PRODUCT_NOT_FOUND -> {
                log.info("Product {} is not a flash sale item- routing normally", request.getProductId());

                Order order = buildOrder(request ,Order.OrderStatus.NORMAL);
                orderRepository.save(order);

                // send the data to inventory before saving in db
                yield OrderResponse.normal(order.getOrderId());
            }
            case INSUFFICIENT_STOCK -> {
                log.info("Insufficient stock for order request for productId : {}", request.getProductId());

                Order order = buildOrder(request, Order.OrderStatus.CANCELLED);
                orderRepository.save(order);

                yield OrderResponse.cancelled(order.getOrderId());
            }

            case SUCCESS -> {
                log.info("Order request for productId : {}", request.getProductId());

                Order order = buildOrder(request, Order.OrderStatus.CONFIRMED);
                orderRepository.save(order);

                yield OrderResponse.confirmed(order.getOrderId(), result.getRemainingStock());
            }

            case ERROR -> {
                log.error("Redis error for productId: {} - routing to normal flow",
                        request.getProductId());

                Order order = buildOrder(request, Order.OrderStatus.NORMAL);
                orderRepository.save(order);

                yield OrderResponse.normal(order.getOrderId());


            }
        };
    }


    // Helper to build Order entity from request
    private Order buildOrder(OrderRequest request, Order.OrderStatus status) {
        return Order.builder()
                .productId(request.getProductId())
                .email(request.getEmail())
                .quantity(request.getQuantity())
                .totalPrice(request.getTotalPrice())
                .status(status)
                .build();
    }
}
