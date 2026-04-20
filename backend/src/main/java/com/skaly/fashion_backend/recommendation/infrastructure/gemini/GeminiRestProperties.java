package com.skaly.fashion_backend.recommendation.infrastructure.gemini;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Cấu hình gọi trực tiếp REST Google Generative Language API (Gemini) — tách khỏi Domain.
 */
@ConfigurationProperties(prefix = "application.gemini.rest")
public record GeminiRestProperties(
        boolean enabled,
        String apiKey,
        String model,
        Duration readTimeout) {

    public GeminiRestProperties {
        if (model == null || model.isBlank()) {
            model = "gemini-1.5-flash";
        }
        if (readTimeout == null) {
            readTimeout = Duration.ofSeconds(30);
        }
    }
}
