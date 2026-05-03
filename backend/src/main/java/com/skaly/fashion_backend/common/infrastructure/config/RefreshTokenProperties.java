package com.skaly.fashion_backend.common.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "application.security.refresh-token")
public class RefreshTokenProperties {
    private long expiration = 604800000L; // 7 days default
    private int maxTokensPerUser = 5;
}
