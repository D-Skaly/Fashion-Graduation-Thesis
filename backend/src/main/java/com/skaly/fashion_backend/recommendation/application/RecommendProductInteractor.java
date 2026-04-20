package com.skaly.fashion_backend.recommendation.application;

import com.skaly.fashion_backend.recommendation.domain.model.FashionIntentResult;
import com.skaly.fashion_backend.recommendation.domain.model.ProductRecommendationResponse;
import com.skaly.fashion_backend.recommendation.domain.model.RecommendedProduct;
import com.skaly.fashion_backend.recommendation.domain.port.AIModelPort;
import com.skaly.fashion_backend.recommendation.domain.port.SemanticProductSearchPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Use-case điều phối: phân tích ý định (Port LLM) → truy vấn vector (Port search) → tổng hợp lời tư vấn (Port LLM).
 * <p>
 * Lớp này thuộc Application: được phép phụ thuộc Spring ({@code @Component}) nhưng không chứa chi tiết HTTP/SDK của Gemini —
 * toàn bộ gọi ra ngoài qua {@link AIModelPort} / {@link SemanticProductSearchPort}.
 */
@Component
@RequiredArgsConstructor
public class RecommendProductInteractor {

    private final AIModelPort aiModelPort;
    private final SemanticProductSearchPort semanticProductSearchPort;

    /**
     * Luồng RAG chuẩn: intent → embedding query (đã tinh chỉnh bởi LLM) → top-K sản phẩm → câu trả lời stylist.
     */
    public ProductRecommendationResponse execute(String userMessage, int maxProducts) {
        FashionIntentResult intent = aiModelPort.interpretUserIntent(userMessage);

        if (!intent.productDiscovery()) {
            String reply = aiModelPort.completeChatPrompt(userMessage);
            return ProductRecommendationResponse.chatOnly(reply);
        }

        String queryForVector = intent.retrievalQueryText().isBlank()
                ? userMessage
                : intent.retrievalQueryText();

        List<RecommendedProduct> hits =
                semanticProductSearchPort.searchSimilarProducts(queryForVector, maxProducts);

        String advisory = hits.isEmpty()
                ? aiModelPort.completeChatPrompt(
                        "Người dùng: " + userMessage + "\nKhông tìm thấy sản phẩm khớp vector. Hãy trả lời ngắn gọn, gợi ý cách mô tả lại nhu cầu.")
                : aiModelPort.composeFashionAdvice(userMessage, hits);

        return ProductRecommendationResponse.withRag(advisory, hits);
    }

    /**
     * API tương thích ngược cho controller / demo: trả về một chuỗi duy nhất cho UI đơn giản.
     */
    public String handle(String userMessage) {
        ProductRecommendationResponse response = execute(userMessage, 5);
        return response.userVisibleMessage();
    }
}
