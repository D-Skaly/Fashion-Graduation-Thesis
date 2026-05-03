package com.skaly.fashion_backend.ai.domain.port;

import reactor.core.publisher.Flux;

/**
 * Cổng trừu tượng tới AI Model cho chat assistant.
 * Tuân thủ Clean Architecture: Domain không phụ thuộc framework.
 */
public interface AIModelPort {

    /**
     * Sinh văn bản từ prompt (chat completion).
     */
    String completeChatPrompt(String prompt);

    /**
     * Stream chat response cho real-time UX.
     */
    Flux<String> streamChatPrompt(String prompt);

    /**
     * Tạo embedding vector từ text (cho RAG).
     */
    float[] embedQuery(String text);

    /**
     * Tìm kiếm sản phẩm liên quan qua vector similarity.
     */
    java.util.List<? extends java.io.Serializable> searchRelatedProducts(float[] vector, int topK);
}