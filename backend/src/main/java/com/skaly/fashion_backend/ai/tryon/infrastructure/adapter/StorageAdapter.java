package com.skaly.fashion_backend.ai.tryon.infrastructure.adapter;

import com.skaly.fashion_backend.ai.tryon.domain.port.StoragePort;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class StorageAdapter implements StoragePort {

    private final MinioClient minioClient;

    @Value("${storage.minio.bucket-name:fashion-images}")
    private String bucketName;

    @Value("${storage.minio.url-expiry:3600}")
    private int urlExpirySeconds;

    @Value("${storage.minio.endpoint:http://localhost:9000}")
    private String minioEndpoint;

    @Override
    public void deleteFile(String fileUrl) {
        try {
            // Extract object name from URL or assume it's the object name
            String objectName = extractObjectName(fileUrl);
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            log.info("File deleted: {}/{}", bucketName, objectName);
        } catch (Exception e) {
            log.error("Failed to delete file {}: {}", fileUrl, e.getMessage());
            throw new RuntimeException("Failed to delete file", e);
        }
    }

    @Override
    public String generatePresignedUploadUrl(String objectName, long expirySeconds) {
        try {
            return minioClient.getPresignedObjectUrl(
                    io.minio.http.Method.PUT,
                    bucketName,
                    objectName,
                    null,
                    null,
                    (int) expirySeconds,
                    TimeUnit.SECONDS
            );
        } catch (Exception e) {
            log.error("Failed to generate presigned upload URL for {}: {}", objectName, e.getMessage());
            throw new RuntimeException("Failed to generate presigned URL", e);
        }
    }

    @Override
    public String generatePresignedDownloadUrl(String objectName, long expirySeconds) {
        try {
            return minioClient.getPresignedObjectUrl(
                    io.minio.http.Method.GET,
                    bucketName,
                    objectName,
                    null,
                    null,
                    (int) expirySeconds,
                    TimeUnit.SECONDS
            );
        } catch (Exception e) {
            log.error("Failed to generate presigned download URL for {}: {}", objectName, e.getMessage());
            throw new RuntimeException("Failed to generate presigned URL", e);
        }
    }

    private String extractObjectName(String fileUrl) {
        if (fileUrl == null) return "";
        // If it's a full URL, extract the path after bucket name
        if (fileUrl.startsWith("http")) {
            // Assuming URL pattern: http://endpoint/bucket/object
            int bucketIndex = fileUrl.indexOf(bucketName);
            if (bucketIndex >= 0) {
                return fileUrl.substring(bucketIndex + bucketName.length() + 1);
            }
        }
        // Otherwise assume it's already the object name
        return fileUrl;
    }
}