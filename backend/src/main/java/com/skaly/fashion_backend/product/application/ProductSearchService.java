package com.skaly.fashion_backend.product.application;

import com.skaly.fashion_backend.product.domain.model.Product;
import com.skaly.fashion_backend.product.domain.port.ProductRepository;
import com.skaly.fashion_backend.product.interfaces.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductEmbeddingService productEmbeddingService;

    @Transactional(readOnly = true)
    public List<ProductResponse> searchProductsSemantically(String query, int limit) {
        float[] queryEmbedding = productEmbeddingService.embedQuery(query);
        List<Product> products = productRepository.findTopKByEmbeddingVectorClosestTo(queryEmbedding, limit);
        return products.stream()
                .map(productMapper::toProductResponseFromDomain)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> searchProductsSemanticallyWithFilters(String query, UUID categoryId, BigDecimal minPrice, BigDecimal maxPrice, int limit) {
        float[] queryEmbedding = productEmbeddingService.embedQuery(query);
        List<Product> products = productRepository.searchWithFilters(queryEmbedding, categoryId, minPrice, maxPrice, limit);
        return products.stream()
                .map(productMapper::toProductResponseFromDomain)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(String keyword, Pageable pageable) {
        return productRepository.searchByKeyword(keyword, pageable)
                .map(productMapper::toProductResponseFromDomain);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> filterProducts(UUID categoryId,
                                                BigDecimal minPrice,
                                                BigDecimal maxPrice,
                                                String sortBy,
                                                String sortDirection,
                                                Pageable pageable) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return productRepository.findByFilters(categoryId, minPrice, maxPrice, sortedPageable)
                .map(productMapper::toProductResponseFromDomain);
    }
}

