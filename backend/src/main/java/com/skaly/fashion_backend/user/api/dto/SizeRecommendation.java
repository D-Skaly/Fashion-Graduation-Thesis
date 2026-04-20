package com.skaly.fashion_backend.user.api.dto;

public record SizeRecommendation(
    String size,
    double confidence
) {}
