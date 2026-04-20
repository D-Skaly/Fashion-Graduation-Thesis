package com.skaly.fashion_backend.product.interfaces.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateProductRequest(
        String name,
        String description,
        BigDecimal basePrice,
        UUID categoryId,
        List<ProductVariantDto> variants) {
}

