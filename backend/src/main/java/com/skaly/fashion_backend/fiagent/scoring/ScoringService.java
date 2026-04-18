package com.skaly.fashion_backend.fiagent.scoring;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Core scoring engine for FI-Agent.
 *
 * Implements:
 * S_total = (S_style * M_finance)^w_fit * (1 + w_push * S_business + w_trend * S_market)
 */
@Service
public class ScoringService {

    private static final int PARALLEL_THRESHOLD = 250;

    /**
     * Calculates total score for one product tuple.
     * <p>
     * We clamp all inputs to non-negative values to keep the multiplicative model stable
     * and avoid NaN when fractional exponents are used.
     */
    public double calculateTotalScore(ScoringInput input, ScoringWeights weights) {
        Objects.requireNonNull(input, "input must not be null");
        Objects.requireNonNull(weights, "weights must not be null");

        double sStyle = Math.max(0.0d, input.styleScore());
        double mFinance = Math.max(0.0d, input.financeMultiplier());
        double sBusiness = Math.max(0.0d, input.businessScore());
        double sMarket = Math.max(0.0d, input.marketScore());

        double wFit = Math.max(0.0d, weights.wFit());
        double wPush = weights.wPush();
        double wTrend = weights.wTrend();

        double fitComponent = Math.pow(sStyle * mFinance, wFit);
        double strategyComponent = 1.0d + (wPush * sBusiness) + (wTrend * sMarket);

        return fitComponent * strategyComponent;
    }

    /**
     * Optimized batch score calculator.
     * <ul>
     *     <li>Uses sequential stream for small batches to avoid parallel overhead.</li>
     *     <li>Switches to parallel stream for larger batches.</li>
     *     <li>Returns sorted descending list, ready for "top-N" truncation by caller.</li>
     * </ul>
     */
    public List<ScoredProduct> calculateBatchScores(List<ScoringInput> inputs, ScoringWeights weights) {
        Objects.requireNonNull(inputs, "inputs must not be null");
        Objects.requireNonNull(weights, "weights must not be null");

        Stream<ScoringInput> stream = inputs.size() >= PARALLEL_THRESHOLD
            ? inputs.parallelStream()
            : inputs.stream();

        return stream
            .map(input -> new ScoredProduct(input.productId(), calculateTotalScore(input, weights)))
            .sorted(Comparator.comparingDouble(ScoredProduct::totalScore).reversed())
            .toList();
    }
}
