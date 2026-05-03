package com.skaly.fashion_backend.product.domain.port;

import com.skaly.fashion_backend.product.domain.port.dto.ProductVariantInfo;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Port for cart module to access product information.
 * This interface is implemented in the product module and used by the cart module.
 * Following the Ports & Adapters pattern to maintain modularity.
 */
public interface ProductCartServicePort {

    /**
     * Get product variant information needed by cart module.
     * Returns null if variant not found.
     */
    ProductVariantInfo getProductVariantInfo(UUID variantId);

    /**
     * Check if product variant has sufficient stock.
     */
    boolean hasSufficientStock(UUID variantId, int quantity);

    default Map<UUID, ProductVariantInfo> getProductVariantsBatch(List<UUID> variantIds) {
        return variantIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        this::getProductVariantInfo
                ));
    }
}
