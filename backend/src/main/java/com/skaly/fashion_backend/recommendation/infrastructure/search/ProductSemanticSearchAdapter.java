package com.skaly.fashion_backend.recommendation.infrastructure.search;

import com.skaly.fashion_backend.product.interfaces.dto.ProductResponse;
import com.skaly.fashion_backend.product.application.ProductSearchService;
import com.skaly.fashion_backend.recommendation.domain.model.RecommendedProduct;
import com.skaly.fashion_backend.recommendation.domain.port.SemanticProductSearchPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Anti-corruption layer: map {@link ProductResponse} (ứng dụng hiện có) sang {@link RecommendedProduct} (Domain recommendation).
 */
@Component
@RequiredArgsConstructor
public class ProductSemanticSearchAdapter implements SemanticProductSearchPort {

    private final ProductSearchService productSearchService;

    @Override
    public List<RecommendedProduct> searchSimilarProducts(String semanticQueryText, int maxResults) {
        return productSearchService.searchProductsSemantically(semanticQueryText, maxResults).stream()
                .map(this::toRecommended)
                .toList();
    }

    private RecommendedProduct toRecommended(ProductResponse p) {
        return new RecommendedProduct(p.id(), p.name(), p.description(), p.basePrice());
    }
}
