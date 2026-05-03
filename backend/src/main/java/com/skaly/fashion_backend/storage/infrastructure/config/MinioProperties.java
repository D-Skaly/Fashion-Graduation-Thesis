package com.skaly.fashion_backend.storage.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "storage.minio")
public class MinioProperties {
    private String endpoint = "http://localhost:9000";
    private String bucketName = "fashion-images";
    private int urlExpiry = 3600;
    private String accessKey = "minioadmin";
    private String secretKey = "minioadmin";
}
