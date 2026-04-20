package com.skaly.fashion_backend.product.domain.model;
import lombok.*;
import java.util.UUID;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductImage {
    private UUID id;
    private String url;
    private String altText;
    private Boolean isPrimary;
    private Integer displayOrder;
}

