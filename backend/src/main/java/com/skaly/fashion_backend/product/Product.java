package com.skaly.fashion_backend.product;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    private UUID id;
    private String name;
    private String sku;
    private String slug;
    private String description;
    private String careInstructions;
    private String metaTitle;
    private String metaDescription;
    private BigDecimal basePrice;
    
    @Builder.Default
    private Boolean isActive = true;
    
    @Builder.Default
    private Boolean isFeatured = false;
    
    private Category category;
    
    @Builder.Default
    private Set<ProductVariant> variants = new HashSet<>();
    
    @Builder.Default
    private Set<ProductImage> images = new HashSet<>();
    
    private String brand;
    private String material;
    private String dimensions;
    private BigDecimal weight;
    
    @Builder.Default
    private Set<String> tags = new HashSet<>();
    
    @Builder.Default
    private Long viewCount = 0L;
    
    @Builder.Default
    private Long soldCount = 0L;
    
    @Builder.Default
    private BigDecimal ratingAvg = BigDecimal.ZERO;
    
    private float[] embeddingVector;
    private float[] styleVector;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void addVariant(ProductVariant variant) {
        variants.add(variant);
    }

    public void addImage(ProductImage image) {
        images.add(image);
    }
}

