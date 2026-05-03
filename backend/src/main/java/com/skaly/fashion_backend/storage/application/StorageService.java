package com.skaly.fashion_backend.storage.application;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public String uploadFile(MultipartFile file, String folder) {
        // ✅ Validate file
        validateFile(file);
        try {
            ensureBucketExists();

            String objectName = generateObjectName(file.getOriginalFilename(), folder);

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            log.info("File uploaded successfully: {}/{}", bucketName, objectName);
            return objectName;

        } catch (Exception e) {
            log.error("Failed to upload file: {}", e.getMessage(), e);
            throw new StorageException("Failed to upload file: " + e.getMessage(), e);
        }
    }

    public String uploadFile(MultipartFile file, String folder, String customFilename) {
        // ✅ Validate file
        validateFile(file);
        try {
            ensureBucketExists();

            String extension = getFileExtension(file.getOriginalFilename());
            String objectName = folder + "/" + customFilename + (extension.isEmpty() ? "" : "." + extension);

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            log.info("File uploaded successfully: {}/{}", bucketName, objectName);
            return objectName;

        } catch (Exception e) {
            log.error("Failed to upload file: {}", e.getMessage(), e);
            throw new StorageException("Failed to upload file: " + e.getMessage(), e);
        }
    }

    public String getFileUrl(String objectName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(minioProperties.getBucketName())
                            .object(objectName)
                            .expiry(minioProperties.getUrlExpiry(), TimeUnit.SECONDS)
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to generate URL for {}: {}", objectName, e.getMessage());
            throw new StorageException("Failed to generate file URL", e);
        }
    }

    public String getPermanentUrl(String objectName) {
        return minioProperties.getEndpoint().replace("http://", "https://") + "/" + minioProperties.getBucketName() + "/" + objectName;
    }

    public void deleteFile(String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            log.info("File deleted: {}/{}", bucketName, objectName);
        } catch (Exception e) {
            log.error("Failed to delete file {}: {}", objectName, e.getMessage());
            throw new StorageException("Failed to delete file: " + e.getMessage(), e);
        }
    }

    public boolean fileExists(String objectName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            return true;
        } catch (ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
                return false;
            }
            throw new StorageException("Error checking file existence", e);
        } catch (Exception e) {
            throw new StorageException("Error checking file existence", e);
        }
    }

    private void ensureBucketExists() throws Exception {
        boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucketName).build()
        );
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            log.info("Created bucket: {}", bucketName);
        }
    }

    private String generateObjectName(String originalFilename, String folder) {
        String extension = getFileExtension(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return folder + "/" + uuid + (extension.isEmpty() ? "" : "." + extension);
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") +1).toLowerCase();
    }

    private void validateFile(MultipartFile file) {
        // Check file size (max 10MB)
        long maxSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxSize) {
            throw new StorageException("File size exceeds maximum limit of 10MB");
        }
        
        // Check content type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new StorageException("Only image files are allowed");
        }
        
        // Check file extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new StorageException("Invalid file name");
        }
        
        String extension = getFileExtension(originalFilename);
        Set<String> allowedExtensions = Set.of("jpg", "jpeg", "png", "gif", "webp");
        if (!allowedExtensions.contains(extension.toLowerCase())) {
            throw new StorageException("Allowed file types: " + allowedExtensions);
        }
    }

    public static class StorageException extends RuntimeException {
        public StorageException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
