package com.skaly.fashion_backend.product;

import com.skaly.fashion_backend.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final StorageService storageService;

    @Transactional(readOnly = true)
    public List<ProductImage> getProductImages(UUID productId) {
        return productImageRepository.findByProductIdOrderBySortOrderAsc(productId);
    }

    @Transactional(readOnly = true)
    public ProductImage getPrimaryImage(UUID productId) {
        return productImageRepository.findByProductIdAndIsPrimaryTrue(productId)
                .orElse(null);
    }

    @Transactional
    public ProductImage addImage(UUID productId, String imageUrl, String alt, boolean isPrimary) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        if (isPrimary) {
            productImageRepository.clearPrimaryImage(productId);
        }

        int sortOrder = (int) productImageRepository.countByProductId(productId);

        ProductImage image = ProductImage.builder()
                .product(product)
                .url(imageUrl)
                .alt(alt != null ? alt : product.getName())
                .sortOrder(sortOrder)
                .isPrimary(isPrimary || sortOrder == 0)
                .build();

        return productImageRepository.save(image);
    }

    @Transactional
    public void setPrimaryImage(UUID imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found: " + imageId));

        productImageRepository.clearPrimaryImage(image.getProduct().getId());
        image.setIsPrimary(true);
        productImageRepository.save(image);
    }

    @Transactional
    public void deleteImage(UUID imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found: " + imageId));

        // Delete from storage
        try {
            String objectName = extractObjectNameFromUrl(image.getUrl());
            storageService.deleteFile(objectName);
        } catch (Exception e) {
            log.warn("Failed to delete image from storage: {}", image.getUrl(), e);
        }

        productImageRepository.delete(image);

        // If deleted image was primary, set new primary
        if (Boolean.TRUE.equals(image.getIsPrimary())) {
            List<ProductImage> remainingImages = productImageRepository
                    .findByProductIdOrderBySortOrderAsc(image.getProduct().getId());
            if (!remainingImages.isEmpty()) {
                ProductImage newPrimary = remainingImages.get(0);
                newPrimary.setIsPrimary(true);
                productImageRepository.save(newPrimary);
            }
        }
    }

    @Transactional
    public void updateImageOrder(UUID productId, List<UUID> imageIds) {
        List<ProductImage> images = productImageRepository.findByProductIdOrderBySortOrderAsc(productId);

        for (int i = 0; i < imageIds.size(); i++) {
            final int order = i;
            UUID imageId = imageIds.get(i);
            images.stream()
                    .filter(img -> img.getId().equals(imageId))
                    .findFirst()
                    .ifPresent(img -> {
                        img.setSortOrder(order);
                        productImageRepository.save(img);
                    });
        }
    }

    private String extractObjectNameFromUrl(String url) {
        // Extract object name from URL (implementation depends on your storage structure)
        // Assuming format: https://domain/bucketName/objectName
        String[] parts = url.split("/");
        return parts[parts.length - 1];
    }
}
