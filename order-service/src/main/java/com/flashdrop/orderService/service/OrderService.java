package com.flashdrop.orderService.service;

import com.flashdrop.orderService.client.InventoryServiceClient;
import com.flashdrop.orderService.client.ProductServiceClient;
import com.flashdrop.orderService.client.UserServiceClient;
import com.flashdrop.orderService.config.RedisConfig;
import com.flashdrop.orderService.dto.*;
import com.flashdrop.orderService.entity.Order;
import com.flashdrop.orderService.redis.InventoryResult;
import com.flashdrop.orderService.redis.RedisInventoryService;
import com.flashdrop.orderService.repository.OrderRepository;
import feign.FeignException;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.security.NoSuchAlgorithmException;

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

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private InventoryServiceClient inventoryServiceClient;

    @Autowired
    private ProductServiceClient productServiceClient;
    @Autowired
    private IdempotencyService idempotencyService;


    public OrderResponse createOrder(OrderRequest request, String idempotencyKey) {

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Idempotency-Key header is required");
        }

        if (!userServiceClient.verifyUser(request.getEmail())) {
            log.info("Invalid email address");
            throw new RuntimeException("Invalid email");
        }

        String fingerprint = fingerprint(request);
        String redisKey = idempotencyService.buildKey(request.getEmail(), idempotencyKey);

        // Atomic: claim the key, or find out what happened to it before.
        List<String> claim = idempotencyService.claim(redisKey, fingerprint);

        switch (claim.get(0)) {
            case "DONE" -> {
                log.info("Duplicate request for key {}, returning saved order {}", idempotencyKey, claim.get(1));
                return responseFromExistingOrder(claim.get(1));
            }
            case "IN_PROGRESS" -> throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "This order is already being processed");
            case "MISMATCH" -> throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY, "Idempotency key was already used for a different request");
            default -> { /* NEW: fall through and process */ }
        }

        try {
            OrderResponse response = processOrder(request, idempotencyKey);
            idempotencyService.markDone(redisKey, response.getOrderId(), fingerprint);
            return response;

        } catch (DataIntegrityViolationException e) {
            // Unique (email, idempotency_key) hit: this order was already saved earlier
            // (for example Redis lost the key). Return that order instead of failing.
            Optional<Order> existing =
                    orderRepository.findByEmailAndIdempotencyKey(request.getEmail(), idempotencyKey);
            if (existing.isPresent()) {
                String orderId = existing.get().getOrderId();
                idempotencyService.markDone(redisKey, orderId, fingerprint);
                return responseFromExistingOrder(orderId);
            }
            idempotencyService.release(redisKey);
            throw e;

        } catch (RuntimeException e) {
            // Nothing was completed: free the key so the user can retry with the same key.
            idempotencyService.release(redisKey);
            throw e;
        }
    }

    private OrderResponse processOrder(OrderRequest request, String idempotencyKey) {

        log.info("Received order request for productId : {}", request.getProductId());

        InventoryResult result =
                redisInventoryService.checkAndDeduct(request.getProductId(), request.getQuantity());

        return switch (result.getStatus()) {

            case PRODUCT_NOT_FOUND -> {
                log.info("Product {} is not a flash sale item - routing normally", request.getProductId());

                // CHANGED: buildOrder now receives the idempotency key
                Order order = buildOrder(request, Order.OrderStatus.NORMAL, idempotencyKey);
                orderRepository.save(order);

                KafkaContext kafkaContext = KafkaContext.builder()
                        .orderId(order.getOrderId())
                        .email(order.getEmail())
                        .quantity(order.getQuantity())
                        .skuID(order.getProductId())
                        .status(String.valueOf(order.getStatus()))
                        .build();

                OrderResponse response;
                try {
                    inventoryServiceClient.purchasedHot(kafkaContext);
                    response = OrderResponse.normal(order.getOrderId());

                } catch (FeignException.Conflict e) {
                    // NEW: inventory says sold out (needs the 409 change in inventory-service)
                    order.setStatus(Order.OrderStatus.CANCELLED);
                    orderRepository.save(order);
                    response = OrderResponse.cancelled(order.getOrderId());

                } catch (RuntimeException e) {
                    // NEW: inventory unreachable / failed. Remove the order row so a retry with
                    // the same key does not hit the unique constraint, then let the wrapper
                    // release the Redis key.
                    log.error("Inventory call failed for order {}", order.getOrderId(), e);
                    orderRepository.delete(order);
                    throw e;
                }
                yield response;
            }

            case INSUFFICIENT_STOCK -> {
                log.info("Insufficient stock for order request for productId : {}", request.getProductId());

                Order order = buildOrder(request, Order.OrderStatus.CANCELLED, idempotencyKey);
                orderRepository.save(order);

                yield OrderResponse.cancelled(order.getOrderId());
            }

            case SUCCESS -> {
                log.info("Order request for productId : {}", request.getProductId());

                Order order = buildOrder(request, Order.OrderStatus.CONFIRMED, idempotencyKey);

                // NEW: stock was already deducted in Redis. If the DB save fails, give it back,
                // otherwise a retry would deduct again and stock would leak.
                try {
                    orderRepository.save(order);
                } catch (RuntimeException e) {
                    redisInventoryService.restoreStock(request.getProductId(), request.getQuantity());
                    throw e;
                }

                String key = order.getOrderId();
                KafkaContext kafkaContext = KafkaContext.builder()
                        .orderId(order.getOrderId())
                        .email(order.getEmail())
                        .quantity(order.getQuantity())
                        .skuID(order.getProductId())
                        .status(String.valueOf(order.getStatus()))
                        .build();

                kafkaTemplate.send("orders", key, kafkaContext)
                        .whenComplete((sendResult, exception) -> {
                            if (exception != null) {
                                log.error("Failed to send order {} to Kafka", order.getOrderId(), exception);
                            } else {
                                log.info("Sent to partition {} with offset {}",
                                        sendResult.getRecordMetadata().partition(),
                                        sendResult.getRecordMetadata().offset());
                            }
                        });

                yield OrderResponse.confirmed(order.getOrderId(), result.getRemainingStock());
            }

            case ERROR -> {
                log.error("Redis error for productId: {} - routing to normal flow", request.getProductId());

                Order order = buildOrder(request, Order.OrderStatus.NORMAL, idempotencyKey);
                orderRepository.save(order);

                yield OrderResponse.normal(order.getOrderId());
            }
        };
    }

    /** Rebuilds the response for an order that already exists (duplicate request). */
    private OrderResponse responseFromExistingOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found for key: " + orderId));
        return toResponse(order);
    }

    private OrderResponse toResponse(Order order) {
        return switch (order.getStatus()) {
            // Remaining stock is not stored on the order. If the parameter is a primitive
            // long/int, pass 0 or add an overload without it.
            case CONFIRMED -> OrderResponse.confirmed(order.getOrderId(), null);
            case CANCELLED -> OrderResponse.cancelled(order.getOrderId());
            default -> OrderResponse.normal(order.getOrderId());
        };
    }


    /** Same request body => same fingerprint. Used to catch a key reused for a different order. */
    private String fingerprint(OrderRequest r) {
        // Remove the coupon part if OrderRequest does not have couponCode yet.
        String raw = r.getProductId() + "|" + r.getQuantity() + "|";
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
    /*public OrderResponse createOrder(OrderRequest request , String idempotencyKey) {

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

                String key = order.getOrderId();
                KafkaContext kafkaContext = KafkaContext.builder()
                        .orderId(order.getOrderId())
                        .email(order.getEmail())
                        .quantity(order.getQuantity())
                        .skuID(order.getProductId())
                        .status(String.valueOf(order.getStatus()))
                        .build();


                inventoryServiceClient.purchasedHot(kafkaContext);

               *//* kafkaTemplate.send("orders",key, kafkaContext)
                        .whenComplete((result23, exception) -> {
                            if (exception != null) {
                                // Handle transmission failure (e.g., alert system, retry log)
                                log.error("❌ Failed to send order {} to Kafka", order.getOrderId(), exception);
                            } else {
                                // Metadata contains partition and offset details
                                log.info("✅ Sent to partition {} with offset {}",
                                        result23.getRecordMetadata().partition(),
                                        result23.getRecordMetadata().offset());
                            }
                        });*//*

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

                String key = order.getOrderId();
                KafkaContext kafkaContext = KafkaContext.builder()
                        .orderId(order.getOrderId())
                        .email(order.getEmail())
                        .quantity(order.getQuantity())
                        .skuID(order.getProductId())
                        .status(String.valueOf(order.getStatus()))
                        .build();

                kafkaTemplate.send("orders",key, kafkaContext)
                        .whenComplete((result23, exception) -> {
                            if (exception != null) {
                                // Handle transmission failure (e.g., alert system, retry log)
                                log.error("❌ Failed to send order {} to Kafka", order.getOrderId(), exception);
                            } else {
                                // Metadata contains partition and offset details
                                log.info("✅ Sent to partition {} with offset {}",
                                        result23.getRecordMetadata().partition(),
                                        result23.getRecordMetadata().offset());
                            }
                        });

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
    }*/


    // Helper to build Order entity from request
    private Order buildOrder(OrderRequest request, Order.OrderStatus status, String idempotencyKey) {
        return Order.builder()
                .productId(request.getProductId())
                .email(request.getEmail())
                .quantity(request.getQuantity())
                .totalPrice(request.getTotalPrice())
                .idempotencyKey(idempotencyKey)
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
            args.add("flash:" + keyList.get(i));
            args.add(valueList.get(i));
        }

        log.info("Preload stock request received: {}", redisRequest);

        return redisTemplate.execute(
                preloadStockScript,
                keys,
                args.toArray(new String[0])
        );
    }

    public List<OrderResponseToOrders> getAllOrders(String email) {

        List<Order> orders = orderRepository.findByEmail(email).orElseThrow();
        List<OrderResponseToOrders> list = new ArrayList<>();
        for(Order order : orders){
            if(order==null){
                continue;
            }
            String prodID = order.getProductId();
            ProductResponse productResponse = productServiceClient.getProductById(prodID);

            log.info("Fetched the details from productService");
            OrderResponseToOrders  orderResponse = OrderResponseToOrders.builder()
                    .orderId(order.getOrderId())
                    .email(order.getEmail())
                    .quantity(order.getQuantity())
                    .totalPrice(order.getTotalPrice())
                    .imageUrl(productResponse.getProductImageUrl())
                    .productDescription(productResponse.getProductDescription())
                    .orderDate(order.getOrderDate())
                    .productId(productResponse.getProductId())
                    .status(String.valueOf(order.getStatus()))
                    .build();
            list.add(orderResponse);
            log.info("Fetched the details from orderService");
        }
        return list;
    }
}
