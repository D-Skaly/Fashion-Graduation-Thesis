package com.skaly.fashion_backend.product.application;

import com.skaly.fashion_backend.product.domain.model.Product;
import com.skaly.fashion_backend.product.infrastructure.persistence.jpa.ProductEntity;
import com.skaly.fashion_backend.product.infrastructure.persistence.jpa.ProductImageEntity;
import com.skaly.fashion_backend.product.infrastructure.persistence.jpa.ProductVariantEntity;
import com.skaly.fashion_backend.product.interfaces.dto.ProductImageDto;
import com.skaly.fashion_backend.product.interfaces.dto.ProductResponse;
import com.skaly.fashion_backend.product.interfaces.dto.ProductVariantDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    public ProductResponse toProductResponseFromDomain(Product product) {
        if (product == null) {
            return null;
        }
        String categoryName = product.getCategory() != null ? product.getCategory().getName() : null;
        List<ProductVariantDto> variantDtos = product.getVariants() == null ? List.of() : product.getVariants().stream()
                .map(v -> new ProductVariantDto(
                        v.getId(),
                        v.getSize(),
                        v.getColor(),
                        v.getStockQuantity(),
                        v.getPrice() != null && product.getBasePrice() != null
                                ? v.getPrice().subtract(product.getBasePrice())
                                : BigDecimal.ZERO,
                        v.getSku()))
                .collect(Collectors.toList());
        List<ProductImageDto> imageDtos = product.getImages() == null || product.getImages().isEmpty()
                ? List.of()
                : product.getImages().stream()
                .map(i -> new ProductImageDto(
                        i.getId(),
                        i.getUrl(),
                        i.getAltText(),
                        i.getDisplayOrder(),
                        i.getIsPrimary(),
                        null))
                .collect(Collectors.toList());
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getBasePrice(),
                categoryName,
                variantDtos,
                imageDtos,
                product.getCreatedAt(),
                product.getUpdatedAt());
    }

    public ProductResponse toProductResponse(ProductEntity product) {
        String categoryName = product.getCategory() != null ? product.getCategory().getName() : null;

        List<ProductVariantDto> variantDtos = product.getVariants().stream()
                .map(this::toProductVariantDto)
                .collect(Collectors.toList());

        List<ProductImageDto> imageDtos = product.getImages() != null 
                ? product.getImages().stream()
                    .map(this::toProductImageDto)
                    .collect(Collectors.toList())
                : List.of();

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getBasePrice(),
                categoryName,
                variantDtos,
                imageDtos,
                product.getCreatedAt(),
                product.getUpdatedAt());
    }

    public ProductVariantDto toProductVariantDto(ProductVariantEntity variant) {
        return new ProductVariantDto(
                variant.getId(),
                variant.getSize(),
                variant.getColor(),
                variant.getStockQuantity(),
                variant.getPriceAdjustment(),
                variant.getSkuCode());
    }

    public ProductVariantEntity toProductVariant(ProductVariantDto dto) {
        return ProductVariantEntity.builder()
                .size(dto.size())
                .color(dto.color())
                .stockQuantity(dto.stockQuantity())
                .priceAdjustment(dto.priceAdjustment())
                .skuCode(dto.skuCode())
                .build();
    }

    public ProductImageDto toProductImageDto(ProductImageEntity image) {
        return new ProductImageDto(
                image.getId(),
                image.getUrl(),
                image.getAlt(),
                image.getSortOrder(),
                image.getIsPrimary(),
                image.getCreatedAt());
    }
}

