package com.skaly.fashion_backend.wishlist.domain.model;

import com.skaly.fashion_backend.product.interfaces.dto.ProductResponse;

import java.time.LocalDateTime;
import java.util.UUID;

public record WishlistResponse(
        UUID id,
        UUID productId,
        ProductResponse product,
        LocalDateTime createdAt
) {
}
