package com.skaly.fashion_backend.recommendation.domain.port;

import com.skaly.fashion_backend.recommendation.domain.model.RecommendedProduct;

import java.util.List;

/**
 * Cổng truy vấn sản phẩm theo vector / ngữ nghĩa — tách biệt khỏi LLM để tránh nhầm RAG retrieval với "AI chat".
 * Triển khai thường dùng pgvector, Supabase, hoặc Spring AI VectorStore ở Infrastructure.
 */
public interface SemanticProductSearchPort {

    List<RecommendedProduct> searchSimilarProducts(String semanticQueryText, int maxResults);
}
