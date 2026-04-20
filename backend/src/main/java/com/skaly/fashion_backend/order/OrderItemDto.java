package com.skaly.fashion_backend.order;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemDto(
        UUID id,
        String productName,
        String size,
        String color,
        Integer quantity,
        BigDecimal snapshotPrice,
        BigDecimal subtotal) {
}

