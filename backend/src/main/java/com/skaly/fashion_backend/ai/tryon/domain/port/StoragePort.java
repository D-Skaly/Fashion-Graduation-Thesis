package com.skaly.fashion_backend.ai.tryon.domain.port;

/**
 * Port định nghĩa việc xóa file trong storage (MinIO/S3).
 * Tuân thủ Clean Architecture: Domain không phụ thuộc implementation cụ thể.
 */
public interface StoragePort {
    /**
     * Xóa file dựa trên URL hoặc object name
     */
    void deleteFile(String fileUrl);
    
    /**
     * Tạo Presigned URL để upload file trực tiếp lên storage
     */
    String generatePresignedUploadUrl(String objectName, long expirySeconds);
    
    /**
     * Tạo Presigned URL để download/hiển thị file
     */
    String generatePresignedDownloadUrl(String objectName, long expirySeconds);
}