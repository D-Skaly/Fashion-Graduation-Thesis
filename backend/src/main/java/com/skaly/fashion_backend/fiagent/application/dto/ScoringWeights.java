package com.skaly.fashion_backend.fiagent.application.dto;

/**
 * Weights from the FI-Agent optimization function:
 * S_total = (S_style * M_finance)^w_fit * (1 + w_push * S_business + w_trend * S_market)
 */
public record ScoringWeights(
    double wFit,
    double wPush,
    double wTrend
) {
}
