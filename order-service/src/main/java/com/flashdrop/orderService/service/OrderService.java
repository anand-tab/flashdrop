package com.flashdrop.orderService.service;

import com.flashdrop.orderService.client.UserServiceClient;
import com.flashdrop.orderService.config.RedisConfig;
import com.flashdrop.orderService.dto.OrderRequest;
import com.flashdrop.orderService.dto.OrderResponse;
import com.flashdrop.orderService.dto.RedisRequest;
import com.flashdrop.orderService.entity.Order;
import com.flashdrop.orderService.redis.InventoryResult;
import com.flashdrop.orderService.redis.RedisInventoryService;
import com.flashdrop.orderService.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class OrderService {


    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private RedisInventoryService redisInventoryService;
    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedisScript<String> preloadStockScript;




    public OrderResponse createOrder(OrderRequest request) {

        if(!userServiceClient.verifyUser(request.getEmail())) {
            log.info("Invalid email address");
           throw new RuntimeException("Invalid email");
        }
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

    public String preLoadStock(RedisRequest redisRequest) {

        List<String> keys = Collections.emptyList();

        List<String> args = new ArrayList<>();

        // expiry time in seconds
        args.add("3600");

        List<String> keyList = redisRequest.getKey();
        List<String> valueList = redisRequest.getValue();

        if (keyList.size() != valueList.size()) {
            throw new RuntimeException("Keys and values size mismatch");
        }

        for (int i = 0; i < keyList.size(); i++) {
            args.add(keyList.get(i));
            args.add(valueList.get(i));
        }

        log.info("Preload stock request received: {}", redisRequest);

        return redisTemplate.execute(
                preloadStockScript,
                keys,
                args.toArray(new String[0])
        );
    }
}
