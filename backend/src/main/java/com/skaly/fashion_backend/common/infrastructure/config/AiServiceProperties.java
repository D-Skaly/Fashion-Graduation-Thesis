package com.skaly.fashion_backend.common.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.ai.service")
public class AiServiceProperties {
    private String url = "http://localhost:8001";
}
