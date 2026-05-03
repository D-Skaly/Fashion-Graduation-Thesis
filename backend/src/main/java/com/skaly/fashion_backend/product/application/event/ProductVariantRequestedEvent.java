package com.skaly.fashion_backend.product.application.event;

import java.util.UUID;

public record ProductVariantRequestedEvent(
    UUID cartId,
    UUID productVariantId
) {}
