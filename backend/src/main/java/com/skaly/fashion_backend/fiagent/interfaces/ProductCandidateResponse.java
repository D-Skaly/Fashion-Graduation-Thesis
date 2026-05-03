package com.skaly.fashion_backend.fiagent.interfaces;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductCandidateResponse(
    UUID id,
    String name,
    String brand,
    BigDecimal basePrice
) {
}
