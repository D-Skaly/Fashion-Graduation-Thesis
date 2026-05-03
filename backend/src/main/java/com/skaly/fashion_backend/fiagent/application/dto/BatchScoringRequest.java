package com.skaly.fashion_backend.fiagent.application.dto;

import com.skaly.fashion_backend.fiagent.application.dto.ScoringInput;
import com.skaly.fashion_backend.fiagent.application.dto.ScoringWeights;
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
