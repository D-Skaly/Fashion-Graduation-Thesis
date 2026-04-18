package com.skaly.fashion_backend.product;

import com.skaly.fashion_backend.common.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductInventoryService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductCacheService productCacheService;

    @Transactional(readOnly = true)
    public ProductVariant getProductVariantById(UUID id) {
        return productVariantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product Variant not found with id: " + id));
    }

    @Transactional
    public void reduceStock(UUID variantId, Integer quantity) {
        ProductVariant variant = productVariantRepository.findByIdForUpdate(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Product Variant not found with id: " + variantId));

        if (variant.getStockQuantity() < quantity) {
            throw new IllegalStateException(
                    "Not enough stock for product: " + variant.getProduct().getName() + " " + variant.getSize());
        }

        variant.setStockQuantity(variant.getStockQuantity() - quantity);
        productVariantRepository.save(variant);
    }

    @Transactional
    public void incrementProductViewCount(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        product.incrementViewCount();
        productRepository.save(product);

        // Evict cache since view count changed
        productCacheService.evictProduct(productId);
    }
}
