package com.skaly.fashion_backend.config;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class StorageConfig {

    @Value("${storage.minio.endpoint:http://localhost:9000}")
    private String minioEndpoint;

    @Value("${storage.minio.access-key:minioadmin}")
    private String accessKey;

    @Value("${storage.minio.secret-key:minioadmin}")
    private String secretKey;

    @Value("${storage.minio.bucket-name:fashion-images}")
    private String bucketName;

    @Bean
    public MinioClient minioClient() {
        log.info("Initializing MinIO client at: {}", minioEndpoint);
        return MinioClient.builder()
                .endpoint(minioEndpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    @Bean
    public String bucketName() {
        return bucketName;
    }
}
