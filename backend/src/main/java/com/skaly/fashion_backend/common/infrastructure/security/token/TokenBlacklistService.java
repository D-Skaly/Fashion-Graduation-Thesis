package com.skaly.fashion_backend.common.infrastructure.security.token;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String BLACKLIST_PREFIX = "token:blacklist:";
    private static final String USER_TOKENS_PREFIX = "user:tokens:";

    /**
     * Add a JWT token to blacklist
     * @param token the JWT token
     * @param expiryDate when the token naturally expires
     */
    public void blacklistToken(String token, Instant expiryDate) {
        String key = BLACKLIST_PREFIX + token;
        long ttlSeconds = Duration.between(Instant.now(), expiryDate).getSeconds();

        if (ttlSeconds > 0) {
            redisTemplate.opsForValue().set(key, "blacklisted", Duration.ofSeconds(ttlSeconds));
            log.info("Token blacklisted for {} seconds", ttlSeconds);
        }
    }

    /**
     * Check if a token is blacklisted
     */
    public boolean isBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * Track user's active tokens for logout all functionality
     */
    public void addUserToken(String userId, String token, Instant expiryDate) {
        String key = USER_TOKENS_PREFIX + userId;
        long ttlSeconds = Duration.between(Instant.now(), expiryDate).getSeconds();

        if (ttlSeconds > 0) {
            redisTemplate.opsForSet().add(key, token);
            redisTemplate.expire(key, Duration.ofSeconds(ttlSeconds));
        }
    }

    /**
     * Blacklist all tokens for a user (logout all devices)
     */
    public void blacklistAllUserTokens(String userId, Instant expiryDate) {
        String key = USER_TOKENS_PREFIX + userId;
        var tokens = redisTemplate.opsForSet().members(key);

        if (tokens != null) {
            for (String token : tokens) {
                blacklistToken(token, expiryDate);
            }
            redisTemplate.delete(key);
            log.info("All tokens blacklisted for user: {}", userId);
        }
    }
}