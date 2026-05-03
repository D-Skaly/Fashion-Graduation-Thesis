package com.skaly.fashion_backend.ai.domain.port;

import java.util.UUID;

/**
 * Port for accessing Product information from ai module.
 * Decouples ai module from product module internals.
 * Follows RULE-2: NO cross-module repository access.
 */
public interface ProductInfoPort {
    
    /**
     * Get basic product info for AI processing (try-on, recommendations).
     */
    ProductInfo getProductInfo(UUID productId);
    
    /**
     * Check if a product exists.
     */
    boolean existsById(UUID productId);
    
    /**
     * DTO for product information needed by AI module.
     */
    record ProductInfo(
        UUID id,
        String name,
        String categoryName,
        String material,
        String description
    ) {}
}
