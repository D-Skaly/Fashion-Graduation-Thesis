package com.skaly.fashion_backend.ai.domain.port;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Port trừu tượng để Size Recommendation dùng product & body profile từ module
 * khác.
 * <p>
 * Tất cả dữ liệu được map về DTO trung lập — không import Domain Model của
 * Product hay User module.
 */
public interface SizeRecommendationPort {

    ProductInfo getProductInfo(UUID productId);

    BodyMeasurements getBodyMeasurements(UUID userId);

    record ProductInfo(
            UUID id,
            String name,
            String categoryName,
            String material,
            String description,
            BigDecimal price) {
    }

    record BodyMeasurements(
            Double height,
            Double weight,
            Double chest,
            Double waist,
            Double hips) {
    }
}
