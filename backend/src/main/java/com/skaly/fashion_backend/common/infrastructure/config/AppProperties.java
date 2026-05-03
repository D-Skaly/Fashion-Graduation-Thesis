package com.skaly.fashion_backend.common.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.frontend")
public class AppProperties {
    private String url = "http://localhost:3000";
}
