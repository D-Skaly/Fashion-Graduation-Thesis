package com.skaly.fashion_backend.fiagent.scoring;

import java.util.UUID;

public record ScoredProduct(
    UUID productId,
    double totalScore
) {
}
