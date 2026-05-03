package com.skaly.fashion_backend.fiagent.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Query payload for style-vector top-K retrieval.
 */
public record StyleVectorSearchRequest(
    @NotEmpty List<Float> styleVector,
    @Min(1) @Max(100) int limit
) {
}
