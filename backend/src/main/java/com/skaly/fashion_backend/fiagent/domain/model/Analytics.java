package com.skaly.fashion_backend.fiagent.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Analytics {

    private UUID id;

    private UUID productId;

    private LocalDate analyticsDate;

    /**
     * CR = conversion rate, range [0, 1].
     */
    @Builder.Default
    private Double conversionRate = 0.0d;

    /**
     * Gap analysis score, range [0, 1], where higher means larger opportunity gap.
     */
    @Builder.Default
    private Double gapAnalysisScore = 0.0d;

    /**
     * M_finance multiplier from specification formula.
     */
    @Builder.Default
    private Double financeMultiplier = 1.0d;

    /**
     * S_business component from specification formula.
     */
    @Builder.Default
    private Double businessScore = 0.0d;

    /**
     * S_market component from specification formula.
     */
    @Builder.Default
    private Double marketScore = 0.0d;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
