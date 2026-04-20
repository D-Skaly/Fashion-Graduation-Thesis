package com.skaly.fashion_backend.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String description,
        BigDecimal basePrice,
        String categoryName,
        List<ProductVariantDto> variants,
        List<ProductImageDto> images,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}

