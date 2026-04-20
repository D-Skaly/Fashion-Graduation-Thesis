package com.skaly.fashion_backend.product.application;

import com.skaly.fashion_backend.common.ResourceNotFoundException;
import com.skaly.fashion_backend.product.application.event.ProductCreatedEvent;
import com.skaly.fashion_backend.product.domain.model.Category;
import com.skaly.fashion_backend.product.domain.model.Product;
import com.skaly.fashion_backend.product.domain.model.ProductVariant;
import com.skaly.fashion_backend.product.domain.port.CategoryRepository;
import com.skaly.fashion_backend.product.domain.port.ProductRepository;
import com.skaly.fashion_backend.product.interfaces.dto.CreateProductRequest;
import com.skaly.fashion_backend.product.interfaces.dto.ProductResponse;
import com.skaly.fashion_backend.product.interfaces.dto.ProductVariantDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

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
        Category category = categoryRepository.findCategoryById(request.categoryId())
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
                BigDecimal adj = variantDto.priceAdjustment() != null ? variantDto.priceAdjustment() : BigDecimal.ZERO;
                BigDecimal variantPrice = request.basePrice().add(adj);
                ProductVariant variant = ProductVariant.builder()
                        .id(variantDto.id())
                        .sku(variantDto.skuCode())
                        .size(variantDto.size())
                        .color(variantDto.color())
                        .stockQuantity(variantDto.stockQuantity())
                        .price(variantPrice)
                        .build();
                product.addVariant(variant);
            }
        }

        Product savedProduct = productRepository.save(product);
        eventPublisher.publishEvent(new ProductCreatedEvent(this, savedProduct.getId()));

        productCacheService.evictBrands();
        productCacheService.evictTags();

        return productMapper.toProductResponseFromDomain(savedProduct);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toProductResponseFromDomain);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByBrand(String brand, Pageable pageable) {
        return productRepository.findByBrandAndIsActiveTrue(brand, pageable)
                .map(productMapper::toProductResponseFromDomain);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByTag(String tag, Pageable pageable) {
        return productRepository.findByTag(tag, pageable)
                .map(productMapper::toProductResponseFromDomain);
    }
}

