package com.skaly.fashion_backend.product;

import com.skaly.fashion_backend.common.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final ProductEmbeddingService productEmbeddingService;
    private final ProductCacheService productCacheService;

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Category not found with id: " + request.categoryId()));

        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .basePrice(request.basePrice())
                .category(category)
                .build();

        if (request.variants() != null) {
            for (ProductVariantDto variantDto : request.variants()) {
                ProductVariant variant = productMapper.toProductVariant(variantDto);
                product.addVariant(variant);
            }
        }

        Product savedProduct = productRepository.save(product);
        eventPublisher.publishEvent(new ProductCreatedEvent(this, savedProduct.getId()));
        
        // Evict cache
        productCacheService.evictBrands();
        productCacheService.evictTags();
        
        return productMapper.toProductResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toProductResponse);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID id) {
        return productCacheService.getProductById(id);
    }

    @Transactional(readOnly = true)
    public ProductVariant getProductVariantById(UUID id) {
        return productVariantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product Variant not found with id: " + id));
    }

    @Transactional
    public void reduceStock(UUID variantId, Integer quantity) {
        ProductVariant variant = getProductVariantById(variantId);

        if (variant.getStockQuantity() < quantity) {
            throw new IllegalStateException(
                    "Not enough stock for product: " + variant.getProduct().getName() + " " + variant.getSize());
        }

        variant.setStockQuantity(variant.getStockQuantity() - quantity);
        productVariantRepository.save(variant);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> searchProductsSemantically(String query, int limit) {
        float[] queryEmbedding = productEmbeddingService.embedQuery(query);
        List<Product> products = productRepository.findTopKByEmbeddingVectorClosestTo(queryEmbedding, limit);
        return products.stream()
                .map(productMapper::toProductResponse)
                .collect(Collectors.toList());
    }

    // Search and Filter Methods

    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(String keyword, Pageable pageable) {
        return productRepository.searchByKeyword(keyword, pageable)
                .map(productMapper::toProductResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> filterProducts(UUID categoryId, BigDecimal minPrice, BigDecimal maxPrice,
                                                String sortBy, String sortDirection, Pageable pageable) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return productRepository.findByFilters(categoryId, minPrice, maxPrice, sortedPageable)
                .map(productMapper::toProductResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getFeaturedProducts(Pageable pageable) {
        return productCacheService.getFeaturedProducts(pageable);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getNewArrivals(Pageable pageable) {
        return productCacheService.getNewArrivals(pageable);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByBrand(String brand, Pageable pageable) {
        return productRepository.findByBrandAndIsActiveTrue(brand, pageable)
                .map(productMapper::toProductResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByTag(String tag, Pageable pageable) {
        return productRepository.findByTag(tag, pageable)
                .map(productMapper::toProductResponse);
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
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        product.incrementViewCount();
        productRepository.save(product);
        
        // Evict cache since view count changed
        productCacheService.evictProduct(productId);
    }
}
