package com.skaly.fashion_backend.product.infrastructure.persistence.jpa;

import com.skaly.fashion_backend.product.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements ProductRepository, CategoryRepository, ProductVariantRepository, ProductImageRepository {

    private final JpaProductRepository jpaProductRepository;
    private final JpaCategoryRepository jpaCategoryRepository;
    private final JpaProductVariantRepository jpaProductVariantRepository;
    private final JpaProductImageRepository jpaProductImageRepository;
    private final ProductPersistenceMapper mapper;

    @Override
    public Product save(Product product) {
        ProductEntity entity = mapper.toEntity(product);
        ProductEntity savedEntity = jpaProductRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return jpaProductRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Category> findCategoryById(UUID id) {
        return jpaCategoryRepository.findById(id).map(mapper::toCategoryDomain);
    }

    @Override
    public Category save(Category category) {
        CategoryEntity entity = mapper.toCategoryEntity(category);
        return mapper.toCategoryDomain(jpaCategoryRepository.save(entity));
    }

    @Override
    public Optional<ProductVariant> findVariantById(UUID id) {
        return jpaProductVariantRepository.findById(id).map(this::toVariantDomain);
    }

    @Override
    public ProductVariant save(ProductVariant variant) {
        ProductVariantEntity entity = toVariantEntity(variant);
        return toVariantDomain(jpaProductVariantRepository.save(entity));
    }

    @Override
    public List<ProductImageEntity> findByProductIdOrderBySortOrderAsc(UUID productId) {
        return jpaProductImageRepository.findByProductIdOrderBySortOrderAsc(productId);
    }

    @Override
    public Optional<ProductImageEntity> findByProductIdAndIsPrimaryTrue(UUID productId) {
        return jpaProductImageRepository.findByProductIdAndIsPrimaryTrue(productId);
    }

    @Override
    public long countByProductId(UUID productId) {
        return jpaProductImageRepository.countByProductId(productId);
    }

    @Override
    public void clearPrimaryImage(UUID productId) {
        jpaProductImageRepository.clearPrimaryImage(productId);
    }

    @Override
    public ProductImageEntity save(ProductImageEntity image) {
        return jpaProductImageRepository.save(image);
    }

    @Override
    public Optional<ProductImageEntity> findById(UUID id) {
        return jpaProductImageRepository.findById(id);
    }

    @Override
    public void delete(ProductImageEntity image) {
        jpaProductImageRepository.delete(image);
    }

    private ProductVariant toVariantDomain(ProductVariantEntity entity) {
        BigDecimal price = entity.getProduct().getBasePrice();
        if (entity.getPriceAdjustment() != null) {
            price = price.add(entity.getPriceAdjustment());
        }
        return ProductVariant.builder()
                .id(entity.getId())
                .sku(entity.getSkuCode())
                .size(entity.getSize())
                .color(entity.getColor())
                .stockQuantity(entity.getStockQuantity())
                .price(price)
                .build();
    }

    private ProductVariantEntity toVariantEntity(ProductVariant domain) {
        ProductEntity productEntity = jpaProductRepository.findById(
                jpaProductVariantRepository.findById(domain.getId())
                        .map(v -> v.getProduct().getId())
                        .orElseThrow(() -> new RuntimeException("Product not found for variant"))
        ).orElseThrow();

        return ProductVariantEntity.builder()
                .id(domain.getId())
                .product(productEntity)
                .skuCode(domain.getSku())
                .size(domain.getSize())
                .color(domain.getColor())
                .stockQuantity(domain.getStockQuantity())
                .priceAdjustment(domain.getPrice().subtract(productEntity.getBasePrice()))
                .build();
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return jpaProductRepository.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    public List<Product> findTopKByEmbeddingVectorClosestTo(float[] vector, int limit) {
        return jpaProductRepository.findTopKByEmbeddingVectorClosestTo(vector, limit).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findTopKByStyleVectorClosestTo(float[] vector, int limit) {
        return jpaProductRepository.findTopKByStyleVectorClosestTo(vector, limit).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Product> searchByKeyword(String keyword, Pageable pageable) {
        return jpaProductRepository.searchByKeyword(keyword, pageable).map(mapper::toDomain);
    }

    @Override
    public Page<Product> findByFilters(UUID categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return jpaProductRepository.findByFilters(categoryId, minPrice, maxPrice, pageable).map(mapper::toDomain);
    }

    @Override
    public Page<Product> findFeaturedProducts(Pageable pageable) {
        return jpaProductRepository.findFeaturedProducts(pageable).map(mapper::toDomain);
    }

    @Override
    public Page<Product> findNewArrivals(Pageable pageable) {
        return jpaProductRepository.findNewArrivals(pageable).map(mapper::toDomain);
    }

    @Override
    public Page<Product> findByBrandAndIsActiveTrue(String brand, Pageable pageable) {
        return jpaProductRepository.findByBrandAndIsActiveTrue(brand, pageable).map(mapper::toDomain);
    }

    @Override
    public Page<Product> findByTag(String tag, Pageable pageable) {
        return jpaProductRepository.findByTag(tag, pageable).map(mapper::toDomain);
    }

    @Override
    public List<String> findAllBrands() {
        return jpaProductRepository.findAllBrands();
    }

    @Override
    public List<String> findAllTags() {
        return jpaProductRepository.findAllTags();
    }
}
