package com.skaly.fashion_backend.fiagent.interfaces;

import com.skaly.fashion_backend.fiagent.scoring.ScoredProduct;
import com.skaly.fashion_backend.fiagent.scoring.ScoringService;
import com.skaly.fashion_backend.product.domain.model.Product;
import com.skaly.fashion_backend.product.domain.port.ProductRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Spring REST surface for FI-Agent data access.
 *
 * This controller intentionally stays "data + scoring" focused.
 * Final natural-language reasoning is delegated to the NestJS orchestration layer.
 */
@RestController
@RequestMapping("/api/v1/fi-agent")
@RequiredArgsConstructor
public class FiAgentController {

    private final ProductRepository productRepository;
    private final ScoringService scoringService;

    @PostMapping("/stylist/top-products")
    public List<ProductCandidateResponse> getTopProductsByStyleVector(
        @Valid @RequestBody StyleVectorSearchRequest request
    ) {
        float[] vector = new float[request.styleVector().size()];
        for (int i = 0; i < request.styleVector().size(); i++) {
            vector[i] = request.styleVector().get(i);
        }

        return productRepository.findTopKByStyleVectorClosestTo(vector, request.limit()).stream()
            .map(this::toCandidate)
            .toList();
    }

    @PostMapping("/scoring/batch")
    public List<ScoredProduct> calculateBatchScores(@Valid @RequestBody BatchScoringRequest request) {
        return scoringService.calculateBatchScores(request.products(), request.weights());
    }

    private ProductCandidateResponse toCandidate(Product product) {
        return new ProductCandidateResponse(
            product.getId(),
            product.getName(),
            product.getBrand(),
            product.getBasePrice()
        );
    }
}
