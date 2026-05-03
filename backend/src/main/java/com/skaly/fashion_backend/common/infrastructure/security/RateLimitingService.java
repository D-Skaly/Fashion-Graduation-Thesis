package com.skaly.fashion_backend.common.infrastructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitingService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String RATE_LIMIT_PREFIX = "ratelimit:";
    private static final int DEFAULT_LIMIT = 100; // requests per minute
    private static final int DEFAULT_WINDOW = 60; // seconds

    public boolean isAllowed(String key, int limit, int windowSeconds) {
        String redisKey = RATE_LIMIT_PREFIX + key;
        
        Long currentCount = redisTemplate.opsForValue().increment(redisKey);
        
        if (currentCount != null && currentCount == 1) {
            // First request, set expiration
            redisTemplate.expire(redisKey, windowSeconds, TimeUnit.SECONDS);
        }
        
        boolean allowed = currentCount != null && currentCount <= limit;
        
        if (!allowed) {
            log.warn("Rate limit exceeded for key: {}", key);
        }
        
        return allowed;
    }

    public boolean isAllowed(String key) {
        return isAllowed(key, DEFAULT_LIMIT, DEFAULT_WINDOW);
    }

    public String getClientKey(String clientId, String endpoint) {
        return clientId + ":" + endpoint;
    }
}