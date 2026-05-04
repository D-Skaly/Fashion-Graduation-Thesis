package com.skaly.fashion_backend.order.domain.port;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductVariantInfo(
    UUID variantId,
    String productName,
    String size,
    String color,
    BigDecimal price
) {}
