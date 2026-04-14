package com.skaly.fashion_backend.storage;

import com.skaly.fashion_backend.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageController {

    private final StorageService storageService;

    /**
     * Upload a single image
     */
    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UploadResponse>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "products") String folder) {

        validateImage(file);

        String objectName = storageService.uploadFile(file, folder);
        String url = storageService.getFileUrl(objectName);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(new UploadResponse(objectName, url)));
    }

    /**
     * Upload multiple images
     */
    @PostMapping("/upload-multiple")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UploadResponse>>> uploadMultipleImages(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "folder", defaultValue = "products") String folder) {

        List<UploadResponse> responses = files.stream()
                .map(file -> {
                    validateImage(file);
                    String objectName = storageService.uploadFile(file, folder);
                    String url = storageService.getFileUrl(objectName);
                    return new UploadResponse(objectName, url);
                })
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(responses));
    }

    /**
     * Get image URL
     */
    @GetMapping("/url")
    public ResponseEntity<ApiResponse<String>> getImageUrl(
            @RequestParam("objectName") String objectName) {
        String url = storageService.getFileUrl(objectName);
        return ResponseEntity.ok(ApiResponse.success(url));
    }

    /**
     * Delete an image
     */
    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteImage(
            @RequestParam("objectName") String objectName) {
        storageService.deleteFile(objectName);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    private void validateImage(MultipartFile file) {
        // Check file size (max 10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds 10MB limit");
        }

        // Check content type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }

        // Allowed formats
        List<String> allowedTypes = List.of("image/jpeg", "image/png", "image/webp", "image/gif");
        if (!allowedTypes.contains(contentType)) {
            throw new IllegalArgumentException("Only JPEG, PNG, WebP, and GIF formats are allowed");
        }
    }

    public record UploadResponse(
            String objectName,
            String url
    ) {}
}
