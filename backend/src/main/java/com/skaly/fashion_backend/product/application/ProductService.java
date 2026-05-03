package com.skaly.fashion_backend.product.application;

import com.skaly.fashion_backend.product.infrastructure.persistence.jpa.ProductVariantEntity;
import com.skaly.fashion_backend.product.interfaces.dto.CreateProductRequest;
import com.skaly.fashion_backend.product.interfaces.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Facade service kept for backward compatibility.
 *
 * Responsibilities are delegated to focused services:
 * - ProductManagementService: product lifecycle
 * - ProductInventoryService: stock and variant operations
 * - ProductSearchService: semantic/keyword/filter search
 * - ProductCacheService: cache-backed reads and invalidation
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductManagementService productManagementService;
    private final ProductInventoryService productInventoryService;
    private final ProductSearchService productSearchService;
    private final ProductCacheService productCacheService;

    private static final int MAX_PAGE_SIZE = 100;

    private Pageable enforceMaxPageSize(Pageable pageable) {
        if (pageable.getPageSize() > MAX_PAGE_SIZE) {
            return PageRequest.of(pageable.getPageNumber(), MAX_PAGE_SIZE, pageable.getSort());
        }
        return pageable;
    }

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        return productManagementService.createProduct(request);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productManagementService.getAllProducts(enforceMaxPageSize(pageable));
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID id) {
        return productCacheService.getProductById(id);
    }

    @Transactional(readOnly = true)
    public ProductVariantEntity getProductVariantById(UUID id) {
        return productInventoryService.getProductVariantById(id);
    }

    @Transactional
    public void reduceStock(UUID variantId, Integer quantity) {
        productInventoryService.reduceStock(variantId, quantity);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> searchProductsSemantically(String query, int limit) {
        return productSearchService.searchProductsSemantically(query, limit);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(String keyword, Pageable pageable) {
        return productSearchService.searchProducts(keyword, enforceMaxPageSize(pageable));
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> filterProducts(UUID categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String sortBy,
            String sortDirection,
            Pageable pageable) {
        return productSearchService.filterProducts(categoryId, minPrice, maxPrice, sortBy, sortDirection, enforceMaxPageSize(pageable));
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getFeaturedProducts(Pageable pageable) {
        return productCacheService.getFeaturedProducts(enforceMaxPageSize(pageable));
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getNewArrivals(Pageable pageable) {
        return productCacheService.getNewArrivals(enforceMaxPageSize(pageable));
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByBrand(String brand, Pageable pageable) {
        return productManagementService.getProductsByBrand(brand, enforceMaxPageSize(pageable));
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByTag(String tag, Pageable pageable) {
        return productManagementService.getProductsByTag(tag, enforceMaxPageSize(pageable));
    }

    @Transactional(readOnly = true)
    public List<String> getAllBrands() {
        return productCacheService.getAllBrands();
    }

    @Transactional(readOnly = true)
    public List<String> getAllTags() {
        return productCacheService.getAllTags();
    }

    @Transactional
    public void incrementProductViewCount(UUID productId) {
        productInventoryService.incrementProductViewCount(productId);
    }
}
