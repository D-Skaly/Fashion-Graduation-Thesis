package com.skaly.fashion_backend.product;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    public ProductResponse toProductResponse(Product product) {
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

    public ProductVariantDto toProductVariantDto(ProductVariant variant) {
        return new ProductVariantDto(
                variant.getId(),
                variant.getSize(),
                variant.getColor(),
                variant.getStockQuantity(),
                variant.getPriceAdjustment(),
                variant.getSkuCode());
    }

    public ProductVariant toProductVariant(ProductVariantDto dto) {
        return ProductVariant.builder()
                .size(dto.size())
                .color(dto.color())
                .stockQuantity(dto.stockQuantity())
                .priceAdjustment(dto.priceAdjustment())
                .skuCode(dto.skuCode())
                .build();
    }

    public ProductImageDto toProductImageDto(ProductImage image) {
        return new ProductImageDto(
                image.getId(),
                image.getUrl(),
                image.getAlt(),
                image.getSortOrder(),
                image.getIsPrimary(),
                image.getCreatedAt());
    }
}
