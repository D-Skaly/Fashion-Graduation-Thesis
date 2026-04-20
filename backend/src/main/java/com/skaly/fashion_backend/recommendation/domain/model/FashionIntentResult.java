package com.skaly.fashion_backend.recommendation.domain.model;

import java.util.List;

/**
 * Kết quả phân tích ý định người dùng ở lớp Domain — thuần Java, không biết Gemini hay OpenAI.
 * <p>
 * {@code retrievalQueryText} là tín hiệu trung lập dùng cho bước nhúng (embedding) và truy vấn vector
 * trong Application/Infrastructure, đảm bảo Neutrality của Domain.
 */
public record FashionIntentResult(
        boolean productDiscovery,
        /** Văn bản tối ưu cho bước RAG / similarity search (thường giàu ngữ nghĩa hơn câu gốc). */
        String retrievalQueryText,
        /** Từ khóa rút gọn phục vụ logging, filter hoặc UI — không bắt buộc cho vector search. */
        List<String> extractedKeywords) {

    public static FashionIntentResult generalConversation() {
        return new FashionIntentResult(false, "", List.of());
    }
}
