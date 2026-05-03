package com.skaly.fashion_backend.cart.interfaces.dto;

import java.util.UUID;

public record UpdateCartRequest(
        UUID cartItemId,
        Integer quantity) {
}
