package com.skaly.fashion_backend.recommendation.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Ảnh chụp (snapshot) sản phẩm cho luồng tư vấn — tránh phơi bày JPA entity ra Domain application contract.
 */
public record RecommendedProduct(UUID id, String name, String description, BigDecimal basePrice) {
}
