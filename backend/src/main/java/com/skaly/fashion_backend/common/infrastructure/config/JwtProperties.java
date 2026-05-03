package com.skaly.fashion_backend.common.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "application.security.jwt")
public class JwtProperties {
    private String secretKey = "default-secret-key-change-in-production";
    private long expiration = 86400000L; // 24 hours default
}
