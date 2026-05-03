package com.skaly.fashion_backend.cart.interfaces.dto;

import java.util.UUID;

public record AddToCartRequest(
        UUID productVariantId,
        Integer quantity) {
}
