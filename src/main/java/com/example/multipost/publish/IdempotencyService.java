package com.example.multipost.publish;

import java.time.Duration;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class IdempotencyService {
    private static final Duration REQUEST_TTL = Duration.ofMinutes(10);

    private final StringRedisTemplate redisTemplate;

    public IdempotencyService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean acquirePublishRequest(Long userId, String requestId) {
        String key = "multipost:idempotency:publish:" + userId + ":" + requestId;
        try {
            Boolean acquired = redisTemplate.opsForValue().setIfAbsent(key, "processing", REQUEST_TTL);
            return Boolean.TRUE.equals(acquired);
        } catch (RedisConnectionFailureException ex) {
            return true;
        }
    }
}
