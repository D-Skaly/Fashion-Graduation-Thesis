package com.skaly.fashion_backend.fiagent.scoring;

import java.util.UUID;

/**
 * Immutable input tuple for batch scoring.
 */
public record ScoringInput(
    UUID productId,
    double styleScore,
    double financeMultiplier,
    double businessScore,
    double marketScore
) {
}
