package com.skaly.fashion_backend.product;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductVariantInternalResponse(
    UUID id,
    UUID productId,
    String productName,
    String size,
    String color,
    BigDecimal price,
    int stockQuantity
) {}

