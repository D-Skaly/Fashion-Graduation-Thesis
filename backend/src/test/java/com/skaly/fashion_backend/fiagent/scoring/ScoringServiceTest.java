package com.skaly.fashion_backend.fiagent.scoring;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ScoringServiceTest {

    private final ScoringService scoringService = new ScoringService();

    @Test
    void shouldCalculateScoreUsingSpecificationFormula() {
        ScoringInput input = new ScoringInput(
            UUID.randomUUID(),
            0.8d,
            1.1d,
            0.5d,
            0.2d
        );
        ScoringWeights weights = new ScoringWeights(1.3d, 0.4d, 0.3d);

        double actual = scoringService.calculateTotalScore(input, weights);
        double expected = Math.pow(0.8d * 1.1d, 1.3d) * (1d + 0.4d * 0.5d + 0.3d * 0.2d);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldReturnBatchScoresSortedDescending() {
        ScoringWeights weights = new ScoringWeights(1d, 1d, 1d);
        ScoringInput low = new ScoringInput(UUID.randomUUID(), 0.2d, 1d, 0.1d, 0.1d);
        ScoringInput high = new ScoringInput(UUID.randomUUID(), 0.9d, 1d, 0.4d, 0.2d);

        List<ScoredProduct> result = scoringService.calculateBatchScores(List.of(low, high), weights);

        assertThat(result).hasSize(2);
        assertThat(result.getFirst().productId()).isEqualTo(high.productId());
        assertThat(result.get(0).totalScore()).isGreaterThan(result.get(1).totalScore());
    }
}
