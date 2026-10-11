package com.flashdrop.orderService.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private static final long IN_PROGRESS_TTL_SECONDS = 300;      // 5 minutes
    private static final Duration DONE_TTL = Duration.ofHours(24);

    private final StringRedisTemplate redis;

    @SuppressWarnings("rawtypes")
    private static final DefaultRedisScript<List> CLAIM_SCRIPT = new DefaultRedisScript<>();

    static {
        CLAIM_SCRIPT.setScriptSource(
                new ResourceScriptSource(new ClassPathResource("scripts/idempotency_claim.lua")));
        CLAIM_SCRIPT.setResultType(List.class);
    }

    /** idem:<normalized email>:<client key> */
    public String buildKey(String email, String idempotencyKey) {
        return "idem:" + email.toLowerCase().trim() + ":" + idempotencyKey;
    }

    /**
     * Atomically claims the key or reports its state.
     * Result.get(0) is one of NEW, IN_PROGRESS, DONE (get(1) = orderId), MISMATCH.
     */
    @SuppressWarnings("unchecked")
    public List<String> claim(String redisKey, String fingerprint) {
        List<String> result = (List<String>) redis.execute(
                CLAIM_SCRIPT,
                List.of(redisKey),
                fingerprint,
                String.valueOf(IN_PROGRESS_TTL_SECONDS));

        if (result == null || result.isEmpty()) {
            throw new IllegalStateException("Idempotency script returned no result");
        }
        return result;
    }

    /** Order finished (confirmed, cancelled or normal): remember the result for 24h. */
    public void markDone(String redisKey, String orderId, String fingerprint) {
        redis.opsForValue().set(redisKey, "DONE|" + orderId + "|" + fingerprint, DONE_TTL);
    }

    /** Order failed before completing: free the key so the same key can be retried. */
    public void release(String redisKey) {
        redis.delete(redisKey);
    }
}