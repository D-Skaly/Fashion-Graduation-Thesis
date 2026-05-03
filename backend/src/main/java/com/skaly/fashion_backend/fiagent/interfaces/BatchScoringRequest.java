package com.skaly.fashion_backend.fiagent.interfaces;

import com.skaly.fashion_backend.fiagent.scoring.ScoringInput;
import com.skaly.fashion_backend.fiagent.scoring.ScoringWeights;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * REST payload for batch score evaluation in FI-Agent.
 */
public record BatchScoringRequest(
    @NotNull @Valid ScoringWeights weights,
    @NotEmpty List<@Valid ScoringInput> products
) {
}
