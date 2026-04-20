package com.skaly.fashion_backend.product;

import com.skaly.fashion_backend.common.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductInventoryService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductCacheService productCacheService;

    @Transactional(readOnly = true)
    public ProductVariantInternalResponse getProductVariantInternal(UUID id) {
        ProductVariantEntity variant = productVariantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductEntity Variant not found with id: " + id));
        
        BigDecimal price = variant.getProduct().getBasePrice();
        if (variant.getPriceAdjustment() != null) {
            price = price.add(variant.getPriceAdjustment());
        }

        return new ProductVariantInternalResponse(
                variant.getId(),
                variant.getProduct().getId(),
                variant.getProduct().getName(),
                variant.getSize(),
                variant.getColor(),
                price,
                variant.getStockQuantity()
        );
    }

    @Transactional(readOnly = true)
    public ProductVariantEntity getProductVariantById(UUID id) {
        return productVariantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductEntity Variant not found with id: " + id));
    }

    @Transactional
    public void reduceStock(UUID variantId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        ProductVariantEntity variant = productVariantRepository.findByIdForUpdate(variantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ProductEntity Variant not found with id: " + variantId));

        if (variant.getStockQuantity() < quantity) {
            throw new IllegalStateException(
                    "Not enough stock for product: "
                            + variant.getProduct().getName() + " "
                            + variant.getSize());
        }

        variant.setStockQuantity(variant.getStockQuantity() - quantity);
    }

    @Transactional
    public void incrementProductViewCount(UUID productId) {
        productCacheService.incrementViewCount(productId);
    }

    @Scheduled(fixedDelay = 300000) // Every 5 minutes
    @Transactional
    public void syncViewCountsToDb() {
        log.debug("Starting sync of product view counts from Redis to DB");
        java.util.Map<UUID, Long> viewCounts = productCacheService.getViewCountsAndClear();
        
        if (viewCounts.isEmpty()) {
            log.debug("No view counts to sync");
            return;
        }

        viewCounts.forEach((productId, count) -> {
            try {
                productRepository.findById(productId).ifPresent(product -> {
                    product.setViewCount(product.getViewCount() + count.intValue());
                    productRepository.save(product);
                    // Also evict cache so users see updated view count if it's in the response
                    productCacheService.evictProduct(productId);
                });
            } catch (Exception e) {
                log.error("Failed to sync view count for product: {}", productId, e);
            }
        });
        
        log.info("Successfully synced {} product view counts to DB", viewCounts.size());
    }
}

