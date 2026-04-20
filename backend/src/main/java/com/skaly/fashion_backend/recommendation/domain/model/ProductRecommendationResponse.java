package com.skaly.fashion_backend.recommendation.domain.model;

import java.util.List;

/**
 * Đầu ra của use-case gợi ý sản phẩm: có thể chỉ là chat chung hoặc kèm danh sách RAG.
 */
public record ProductRecommendationResponse(
        String userVisibleMessage,
        List<RecommendedProduct> matchedProducts,
        boolean ragUsed) {

    public static ProductRecommendationResponse chatOnly(String message) {
        return new ProductRecommendationResponse(message, List.of(), false);
    }

    public static ProductRecommendationResponse withRag(String message, List<RecommendedProduct> products) {
        return new ProductRecommendationResponse(message, List.copyOf(products), true);
    }
}
