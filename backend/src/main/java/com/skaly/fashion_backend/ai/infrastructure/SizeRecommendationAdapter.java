package com.skaly.fashion_backend.ai.infrastructure;

import com.skaly.fashion_backend.ai.domain.port.SizeRecommendationPort;
import com.skaly.fashion_backend.product.domain.port.ProductRepository;
import com.skaly.fashion_backend.user.BodyProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Adapter kết nối AI module với Product & User module qua port interfaces.
 * <p>
 * Đây là điểm <b>duy nhất</b> trong AI module được phép nhìn thấy các port của
 * module khác.
 * <p>
 * 
 * @see ProductRepository
 * @see BodyProfileRepository
 */
@Component
@RequiredArgsConstructor
public class SizeRecommendationAdapter implements SizeRecommendationPort {

    private final ProductRepository productRepository;
    private final BodyProfileRepository bodyProfileRepository;

    @Override
    public ProductInfo getProductInfo(UUID productId) {
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new com.skaly.fashion_backend.common.ResourceNotFoundException(
                        "Product not found: " + productId));

        return new ProductInfo(
                product.getId(),
                product.getName(),
                product.getCategory() != null ? product.getCategory().getName() : "General",
                product.getMaterial(),
                product.getDescription(),
                product.getBasePrice());
    }

    @Override
    public BodyMeasurements getBodyMeasurements(UUID userId) {
        var profile = bodyProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new com.skaly.fashion_backend.common.ResourceNotFoundException(
                        "Body profile not found for user: " + userId));

        return new BodyMeasurements(
                profile.getHeight(),
                profile.getWeight(),
                profile.getChest(),
                profile.getWaist(),
                profile.getHips());
    }
}
