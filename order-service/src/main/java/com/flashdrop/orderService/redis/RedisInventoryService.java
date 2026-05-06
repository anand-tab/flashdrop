package com.flashdrop.orderService.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisInventoryService {

    private final StringRedisTemplate redisTemplate;
    private final RedisScript<Long> checkAndDeductScript;

    private static final String KEY_PREFIX = "flash:";

    public InventoryResult checkAndDeduct(String productId, int requestedQty) {
        String key = KEY_PREFIX + productId;

        try {
            // Debug: check if key exists before running script
            String currentValue = redisTemplate.opsForValue().get(key);
            log.info("DEBUG - Key: {}", key);
            log.info("DEBUG - Current value in Redis: {}", currentValue);
            log.info("DEBUG - Requested quantity: {}", requestedQty);

            Long result = redisTemplate.execute(
                    checkAndDeductScript,
                    Collections.singletonList(key),
                    String.valueOf(requestedQty)
            );

            log.info("DEBUG - Lua script result: {}", result);

            if (result == null)   return InventoryResult.error();
            if (result == -1L)    return InventoryResult.notFlashProduct();
            if (result == -2L)    return InventoryResult.insufficientStock();

            return InventoryResult.success(result);

        } catch (Exception e) {
            log.error("Redis error for product {}: {}", productId, e.getMessage());
            log.error("Full error: ", e);  // this prints full stack trace
            return InventoryResult.error();
        }
    }
}