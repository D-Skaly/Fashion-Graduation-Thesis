package com.skaly.fashion_backend.product.application.event;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductVariantResponseEvent(
    UUID cartId,
    UUID productVariantId,
    String productName,
    String size,
    String color,
    BigDecimal basePrice,
    BigDecimal priceAdjustment,
    Integer stockQuantity
) {}
