package com.skaly.fashion_backend.product.domain.model;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductVariant {
    private UUID id;
    private String sku;
    private String size;
    private String color;
    private Integer stockQuantity;
    private BigDecimal price;
}

