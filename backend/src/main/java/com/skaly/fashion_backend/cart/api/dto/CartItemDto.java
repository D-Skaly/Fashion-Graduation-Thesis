package com.skaly.fashion_backend.cart.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemDto(
                UUID id,
                UUID productVariantId,
                String productName,
                String size,
                String color,
                BigDecimal price,
                BigDecimal snapshotPrice,
                Integer quantity,
                BigDecimal subtotal,
                boolean isOutOfStock,
                boolean quantityAdjusted) {
}
