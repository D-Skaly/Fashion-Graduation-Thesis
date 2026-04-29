package com.skaly.fashion_backend.order.application.event;

import java.util.UUID;

public record ClearCartRequestedEvent(
    UUID userId,
    String userEmail
) {}
