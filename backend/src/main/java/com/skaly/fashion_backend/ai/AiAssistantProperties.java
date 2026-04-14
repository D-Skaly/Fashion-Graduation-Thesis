package com.skaly.fashion_backend.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.ai.assistant")
public record AiAssistantProperties(
        boolean enabled,
        int maxMessageLength,
        Retry retry,
        Timeout timeout,
        RateLimit rateLimit) {

    public record Retry(int maxAttempts, long backoffMs) {
    }

    public record Timeout(long responseMs) {
    }

    public record RateLimit(boolean enabled, int maxRequests, int windowSeconds) {
    }
}
