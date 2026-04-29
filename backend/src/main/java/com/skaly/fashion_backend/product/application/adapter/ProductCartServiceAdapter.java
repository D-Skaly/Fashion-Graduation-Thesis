package com.skaly.fashion_backend.product.application.adapter;

import com.skaly.fashion_backend.product.application.ProductInventoryService;
import com.skaly.fashion_backend.product.domain.port.ProductCartServicePort;
import com.skaly.fashion_backend.product.domain.port.dto.ProductVariantInfo;
import com.skaly.fashion_backend.product.infrastructure.persistence.jpa.ProductVariantEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Adapter implementing ProductCartServicePort.
 * Lives in product module, exposes domain data to cart module via port interface.
 */
@Component
@RequiredArgsConstructor
public class ProductCartServiceAdapter implements ProductCartServicePort {

    private final ProductInventoryService productInventoryService;

    @Override
    @Transactional(readOnly = true)
    public ProductVariantInfo getProductVariantInfo(UUID variantId) {
        ProductVariantEntity variant = productInventoryService.getProductVariantById(variantId);
        if (variant == null) {
            return null;
        }

        return new ProductVariantInfo(
                variant.getId(),
                variant.getProduct().getName(),
                variant.getProduct().getBasePrice(),
                variant.getPriceAdjustment(),
                variant.getStockQuantity(),
                variant.getSize(),
                variant.getColor()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasSufficientStock(UUID variantId, int quantity) {
        ProductVariantEntity variant = productInventoryService.getProductVariantById(variantId);
        return variant != null && variant.getStockQuantity() >= quantity;
    }
}
