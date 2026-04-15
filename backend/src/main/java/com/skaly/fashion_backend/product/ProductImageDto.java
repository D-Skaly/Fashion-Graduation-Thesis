package com.skaly.fashion_backend.product;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProductImageDto(
        UUID id,
        String url,
        String alt,
        Integer sortOrder,
        Boolean isPrimary,
        LocalDateTime createdAt
) {
}
