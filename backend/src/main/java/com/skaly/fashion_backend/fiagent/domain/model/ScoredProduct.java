package com.skaly.fashion_backend.fiagent.domain.model;

import java.util.UUID;

public record ScoredProduct(
    UUID productId,
    double totalScore
) {
}
