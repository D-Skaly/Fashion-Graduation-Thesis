package com.skaly.fashion_backend.product.interfaces.dto;

import java.math.BigDecimal;

public record ProductVariantDto(
                java.util.UUID id,
                String size,
                String color,
                Integer stockQuantity,
                BigDecimal priceAdjustment,
                String skuCode) {
}

