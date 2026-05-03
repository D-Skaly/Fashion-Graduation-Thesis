package com.skaly.fashion_backend.common.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "application.security.encryption")
public class EncryptionProperties {
    private String key = "default-encryption-key-32-chars-long!";
}
