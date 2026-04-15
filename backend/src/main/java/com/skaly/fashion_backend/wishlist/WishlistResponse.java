package com.skaly.fashion_backend.wishlist;

import com.skaly.fashion_backend.product.ProductResponse;

import java.time.LocalDateTime;
import java.util.UUID;

public record WishlistResponse(
        UUID id,
        UUID productId,
        ProductResponse product,
        LocalDateTime createdAt
) {
}
