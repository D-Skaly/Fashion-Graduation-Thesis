package com.skaly.fashion_backend.product.application;

import com.skaly.fashion_backend.common.ResourceNotFoundException;
import com.skaly.fashion_backend.product.domain.port.ProductRepository;
import com.skaly.fashion_backend.product.infrastructure.persistence.jpa.JpaProductVariantRepository;
import com.skaly.fashion_backend.product.infrastructure.persistence.jpa.ProductVariantEntity;
import com.skaly.fashion_backend.product.interfaces.dto.ProductVariantInternalResponse;
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
    private final JpaProductVariantRepository jpaProductVariantRepository;
    private final ProductCacheService productCacheService;

    @Transactional(readOnly = true)
    public ProductVariantInternalResponse getProductVariantInternal(UUID id) {
        ProductVariantEntity variant = jpaProductVariantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product variant not found with id: " + id));

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
                variant.getStockQuantity());
    }

    @Transactional(readOnly = true)
    public ProductVariantEntity getProductVariantById(UUID id) {
        return jpaProductVariantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product variant not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public int getCurrentStock(UUID id) {
        return getProductVariantById(id).getStockQuantity();
    }

    @Transactional
    public void reduceStock(UUID variantId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        ProductVariantEntity variant = jpaProductVariantRepository.findByIdForUpdate(variantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product variant not found with id: " + variantId));

        if (variant.getStockQuantity() < quantity) {
            throw new IllegalStateException(
                    "Not enough stock for product: "
                            + variant.getProduct().getName() + " "
                            + variant.getSize());
        }

        variant.setStockQuantity(variant.getStockQuantity() - quantity);
        jpaProductVariantRepository.save(variant);
    }

    @Transactional
    public void addStock(UUID variantId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        ProductVariantEntity variant = jpaProductVariantRepository.findByIdForUpdate(variantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product variant not found with id: " + variantId));

        variant.setStockQuantity(variant.getStockQuantity() + quantity);
        jpaProductVariantRepository.save(variant);
    }

    @Transactional
    public void incrementProductViewCount(UUID productId) {
        productCacheService.incrementViewCount(productId);
    }

    @Scheduled(fixedDelay = 300000)
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
                    long current = product.getViewCount() != null ? product.getViewCount() : 0L;
                    product.setViewCount(current + count.intValue());
                    productRepository.save(product);
                    productCacheService.evictProduct(productId);
                });
            } catch (Exception e) {
                log.error("Failed to sync view count for product: {}", productId, e);
            }
        });

        log.info("Successfully synced {} product view counts to DB", viewCounts.size());
    }
}
