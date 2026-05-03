package com.skaly.fashion_backend.product.domain.model;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder(builderMethodName = "productVariantBuilder")
public class ProductVariant {
    private UUID id;
    private String sku;
    private String size;
    private String color;
    private Integer stockQuantity;
    private BigDecimal price;

    public static ProductVariantBuilder builder() {
        return productVariantBuilder()
                .id(UUID.randomUUID());
    }

    public static class ProductVariantBuilder {
        private UUID id = UUID.randomUUID();

        public ProductVariantBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public ProductVariant build() {
            return new ProductVariant(id, sku, size, color, stockQuantity, price);
        }
    }
}

