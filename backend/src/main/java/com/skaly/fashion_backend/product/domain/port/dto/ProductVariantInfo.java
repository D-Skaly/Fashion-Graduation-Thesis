package com.skaly.fashion_backend.product.domain.port.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for product variant information exposed to cart module.
 * This is a read-only view with only the fields cart needs.
 */
public record ProductVariantInfo(
        UUID id,
        String productName,
        BigDecimal basePrice,
        BigDecimal priceAdjustment,
        Integer stockQuantity,
        String size,
        String color
) {}
