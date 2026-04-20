package com.skaly.fashion_backend.product;

import com.skaly.fashion_backend.common.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductManagementService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final ProductCacheService productCacheService;

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        CategoryEntity category = categoryRepository.findById(request.categoryId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("CategoryEntity not found with id: " + request.categoryId()));

        ProductEntity product = ProductEntity.builder()
                .name(request.name())
                .description(request.description())
                .basePrice(request.basePrice())
                .category(category)
                .build();

        if (request.variants() != null) {
            for (ProductVariantDto variantDto : request.variants()) {
                ProductVariantEntity variant = productMapper.toProductVariant(variantDto);
                product.addVariant(variant);
            }
        }

        ProductEntity savedProduct = productRepository.save(product);
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
    public Page<ProductResponse> getProductsByBrand(String brand, Pageable pageable) {
        return productRepository.findByBrandAndIsActiveTrue(brand, pageable)
                .map(productMapper::toProductResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByTag(String tag, Pageable pageable) {
        return productRepository.findByTag(tag, pageable)
                .map(productMapper::toProductResponse);
    }
}

