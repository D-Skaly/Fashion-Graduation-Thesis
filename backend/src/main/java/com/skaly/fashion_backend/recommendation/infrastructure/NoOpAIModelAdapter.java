package com.skaly.fashion_backend.recommendation.infrastructure;

import com.skaly.fashion_backend.recommendation.domain.model.FashionIntentResult;
import com.skaly.fashion_backend.recommendation.domain.model.RecommendedProduct;
import com.skaly.fashion_backend.recommendation.domain.port.AIModelPort;

import java.util.List;

/**
 * Fallback khi không có ChatModel hay Gemini REST — tránh vỡ context Spring lúc khởi động local tối giản.
 */
public class NoOpAIModelAdapter implements AIModelPort {

    @Override
    public FashionIntentResult interpretUserIntent(String userMessage) {
        return FashionIntentResult.generalConversation();
    }

    @Override
    public String completeChatPrompt(String composedPrompt) {
        return "AI assistant is not configured. Set GEMINI_API_KEY and enable Spring AI or native REST.";
    }

    @Override
    public String composeFashionAdvice(String userMessage, List<RecommendedProduct> candidates) {
        return completeChatPrompt(userMessage);
    }
}
