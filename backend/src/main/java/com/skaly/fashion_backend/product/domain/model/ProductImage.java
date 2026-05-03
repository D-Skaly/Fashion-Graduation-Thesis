package com.skaly.fashion_backend.product.domain.model;
import lombok.*;
import java.util.UUID;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder(builderMethodName = "productImageBuilder")
public class ProductImage {
    private UUID id;
    private String url;
    private String altText;
    private Boolean isPrimary;
    private Integer displayOrder;

    public static ProductImageBuilder builder() {
        return productImageBuilder()
                .id(UUID.randomUUID());
    }

    public static class ProductImageBuilder {
        private UUID id = UUID.randomUUID();

        public ProductImageBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public ProductImage build() {
            return new ProductImage(id, url, altText, isPrimary, displayOrder);
        }
    }
}

