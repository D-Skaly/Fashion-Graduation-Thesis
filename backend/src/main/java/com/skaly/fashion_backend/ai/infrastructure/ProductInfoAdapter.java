package com.skaly.fashion_backend.ai.infrastructure;

import com.skaly.fashion_backend.ai.domain.port.ProductInfoPort;
import com.skaly.fashion_backend.product.domain.port.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.UUID;

/**
 * Adapter implementing ProductInfoPort for the ai module.
 * Decouples ai module from product module internals.
 */
@Component
@RequiredArgsConstructor
public class ProductInfoAdapter implements ProductInfoPort {
    
    private final ProductRepository productRepository;
    
    @Override
    public ProductInfo getProductInfo(UUID productId) {
        return productRepository.findById(productId)
                .map(p -> new ProductInfo(
                        p.getId(),
                        p.getName(),
                        p.getCategory() != null ? p.getCategory().getName() : null,
                        p.getMaterial(),
                        p.getDescription()
                ))
                .orElseThrow(() -> new com.skaly.fashion_backend.common.domain.ResourceNotFoundException(
                        "Product not found: " + productId));
    }
    
    @Override
    public boolean existsById(UUID productId) {
        return productRepository.findById(productId).isPresent();
    }
}
