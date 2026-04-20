package com.skaly.fashion_backend.product.infrastructure.persistence.jpa;

import com.skaly.fashion_backend.product.domain.model.Category;
import com.skaly.fashion_backend.product.domain.model.Product;
import com.skaly.fashion_backend.product.domain.model.ProductImage;
import com.skaly.fashion_backend.product.domain.model.ProductVariant;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Component
public class ProductPersistenceMapper {

    public ProductEntity toEntity(Product domain) {
        if (domain == null) return null;

        ProductEntity entity = ProductEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .sku(domain.getSku())
                .slug(domain.getSlug())
                .description(domain.getDescription())
                .careInstructions(domain.getCareInstructions())
                .metaTitle(domain.getMetaTitle())
                .metaDescription(domain.getMetaDescription())
                .basePrice(domain.getBasePrice())
                .isActive(domain.getIsActive())
                .isFeatured(domain.getIsFeatured())
                .brand(domain.getBrand())
                .material(domain.getMaterial())
                .dimensions(domain.getDimensions())
                .weight(domain.getWeight())
                .tags(domain.getTags())
                .viewCount(domain.getViewCount())
                .soldCount(domain.getSoldCount())
                .ratingAvg(domain.getRatingAvg())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();

        entity.setEmbeddingVector(domain.getEmbeddingVector());
        entity.setStyleVector(domain.getStyleVector());

        if (domain.getCategory() != null) {
            entity.setCategory(toCategoryEntity(domain.getCategory()));
        }

        if (domain.getVariants() != null) {
            entity.setVariants(domain.getVariants().stream()
                    .map(v -> toVariantEntity(v, entity))
                    .collect(Collectors.toSet()));
        }

        if (domain.getImages() != null) {
            entity.setImages(domain.getImages().stream()
                    .map(i -> toImageEntity(i, entity))
                    .collect(Collectors.toSet()));
        }

        return entity;
    }

    public Product toDomain(ProductEntity entity) {
        if (entity == null) return null;

        Product domain = Product.builder()
                .id(entity.getId())
                .name(entity.getName())
                .sku(entity.getSku())
                .slug(entity.getSlug())
                .description(entity.getDescription())
                .careInstructions(entity.getCareInstructions())
                .metaTitle(entity.getMetaTitle())
                .metaDescription(entity.getMetaDescription())
                .basePrice(entity.getBasePrice())
                .isActive(entity.getIsActive())
                .isFeatured(entity.getIsFeatured())
                .brand(entity.getBrand())
                .material(entity.getMaterial())
                .dimensions(entity.getDimensions())
                .weight(entity.getWeight())
                .tags(entity.getTags())
                .viewCount(entity.getViewCount())
                .soldCount(entity.getSoldCount())
                .ratingAvg(entity.getRatingAvg())
                .embeddingVector(entity.getEmbeddingVector())
                .styleVector(entity.getStyleVector())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();

        if (entity.getCategory() != null) {
            domain.setCategory(toCategoryDomain(entity.getCategory()));
        }

        if (entity.getVariants() != null) {
            domain.setVariants(entity.getVariants().stream()
                    .map(this::toVariantDomain)
                    .collect(Collectors.toSet()));
        }

        if (entity.getImages() != null) {
            domain.setImages(entity.getImages().stream()
                    .map(this::toImageDomain)
                    .collect(Collectors.toSet()));
        }

        return domain;
    }

    public CategoryEntity toCategoryEntity(Category domain) {
        if (domain == null) return null;
        return CategoryEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .slug(domain.getSlug())
                .description(domain.getDescription())
                // Parent handling would need a repository lookup or a lazy proxy if needed
                .build();
    }

    public Category toCategoryDomain(CategoryEntity entity) {
        if (entity == null) return null;
        return Category.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .description(entity.getDescription())
                .parentId(entity.getParent() != null ? entity.getParent().getId() : null)
                .build();
    }

    private ProductVariantEntity toVariantEntity(ProductVariant domain, ProductEntity productEntity) {
        return ProductVariantEntity.builder()
                .id(domain.getId())
                .product(productEntity)
                .skuCode(domain.getSku())
                .size(domain.getSize())
                .color(domain.getColor())
                .stockQuantity(domain.getStockQuantity())
                .priceAdjustment(domain.getPrice() != null ? domain.getPrice().subtract(productEntity.getBasePrice()) : null)
                .build();
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

    private ProductImageEntity toImageEntity(ProductImage domain, ProductEntity productEntity) {
        return ProductImageEntity.builder()
                .id(domain.getId())
                .product(productEntity)
                .url(domain.getUrl())
                .alt(domain.getAltText())
                .isPrimary(domain.getIsPrimary())
                .sortOrder(domain.getDisplayOrder())
                .build();
    }

    private ProductImage toImageDomain(ProductImageEntity entity) {
        return ProductImage.builder()
                .id(entity.getId())
                .url(entity.getUrl())
                .altText(entity.getAlt())
                .isPrimary(entity.getIsPrimary())
                .displayOrder(entity.getSortOrder())
                .build();
    }
}

