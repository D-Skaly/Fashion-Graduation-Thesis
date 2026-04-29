package com.skaly.fashion_backend.ai;

import com.skaly.fashion_backend.ai.domain.port.SizeRecommendationPort;
import com.skaly.fashion_backend.recommendation.domain.port.AIModelPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service tư vấn Size phù hợp dựa trên Body Measurement + thông tin sản phẩm.
 * <p>
 * Không phụ thuộc trực tiếp repository hay domain model của Product/User module
 * —
 * toàn bộ giao tiếp qua {@link SizeRecommendationPort}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SizeRecommendationService {

    private final AIModelPort aiModelPort;
    private final SizeRecommendationPort sizeRecommendationPort;

    public SizeRecommendationResponse recommendSize(UUID userId, UUID productId) {
        // 1. Fetch data qua Port trung lập
        var body = sizeRecommendationPort.getBodyMeasurements(userId);
        var product = sizeRecommendationPort.getProductInfo(productId);

        // 2. Build Prompt
        String prompt = buildPrompt(body, product);

        // 3. Call AI
        String aiResponse = aiModelPort.completeChatPrompt(prompt);

        // 4. Return
        return new SizeRecommendationResponse(aiResponse);
    }

    private String buildPrompt(SizeRecommendationPort.BodyMeasurements body,
            SizeRecommendationPort.ProductInfo product) {
        return String.format(
                "You are a professional fashion sizing expert. Based on the following customer body measurements and product details, "
                        +
                        "recommend the best size and explain why.\n\n" +
                        "CUSTOMER MEASUREMENTS:\n" +
                        "- Height: %.1f cm\n" +
                        "- Weight: %.1f kg\n" +
                        "- Chest: %.1f cm\n" +
                        "- Waist: %.1f cm\n" +
                        "- Hips: %.1f cm\n\n" +
                        "PRODUCT DETAILS:\n" +
                        "- Name: %s\n" +
                        "- Category: %s\n" +
                        "- Material: %s\n" +
                        "- Description: %s\n\n" +
                        "Please provide a recommended size (e.g., M, L, XL) and a brief, friendly explanation for the customer.",
                body.height(), body.weight(), body.chest(), body.waist(), body.hips(),
                product.name(), product.categoryName(), product.material(), product.description());
    }

    public record SizeRecommendationResponse(String recommendation) {
    }
}
