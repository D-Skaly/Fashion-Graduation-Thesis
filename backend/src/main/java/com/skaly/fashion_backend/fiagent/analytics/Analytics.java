package com.skaly.fashion_backend.fiagent.analytics;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Daily product-level analytics supporting FI-Agent Module 2 (AI Strategist).
 */
@Entity
@Table(name = "analytics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Analytics {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "analytics_date", nullable = false)
    private LocalDate analyticsDate;

    /**
     * CR = conversion rate, range [0, 1].
     */
    @Builder.Default
    @Column(name = "conversion_rate", nullable = false)
    private Double conversionRate = 0.0d;

    /**
     * Gap analysis score, range [0, 1], where higher means larger opportunity gap.
     */
    @Builder.Default
    @Column(name = "gap_analysis_score", nullable = false)
    private Double gapAnalysisScore = 0.0d;

    /**
     * M_finance multiplier from specification formula.
     */
    @Builder.Default
    @Column(name = "finance_multiplier", nullable = false)
    private Double financeMultiplier = 1.0d;

    /**
     * S_business component from specification formula.
     */
    @Builder.Default
    @Column(name = "business_score", nullable = false)
    private Double businessScore = 0.0d;

    /**
     * S_market component from specification formula.
     */
    @Builder.Default
    @Column(name = "market_score", nullable = false)
    private Double marketScore = 0.0d;

    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
