package com.skaly.fashion_backend.ai.application;

import com.skaly.fashion_backend.ai.domain.AIModelPort;
import com.skaly.fashion_backend.product.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecommendProductInteractor {
    private final AIModelPort aiModelPort;
    private final ProductSearchService productSearchService;

    public String handle(String userMessage) {
        String aiResponse = aiModelPort.generateResponse(userMessage);
        
        // Simple logic to detect "RECOMMEND" intent for demonstration based on test plan
        if (aiResponse.contains("INTENT: RECOMMEND")) {
            // Extract some query from response or just use the message
            productSearchService.searchProductsSemantically(userMessage, 5);
            return "Đang tìm kiếm sản phẩm phù hợp với bạn...";
        }
        
        return aiResponse;
    }
}
