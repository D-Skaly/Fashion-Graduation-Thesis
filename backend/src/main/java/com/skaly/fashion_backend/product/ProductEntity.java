package com.skaly.fashion_backend.product;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@NamedEntityGraph(
    name = "Product.detail",
    attributeNodes = {
        @NamedAttributeNode("category"),
        @NamedAttributeNode("variants"),
        @NamedAttributeNode("images"),
        @NamedAttributeNode("tags")
    }
)
@Table(name = "products", indexes = {
    @Index(name = "idx_product_sku", columnList = "sku", unique = true),
    @Index(name = "idx_product_slug", columnList = "slug", unique = true),
    @Index(name = "idx_product_is_active", columnList = "isActive"),
    @Index(name = "idx_product_is_featured", columnList = "isFeatured"),
    @Index(name = "idx_product_brand", columnList = "brand")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String sku;

    @Column(unique = true, nullable = false)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "care_instructions", columnDefinition = "TEXT")
    private String careInstructions;

    @Column(name = "meta_title", length = 500)
    private String metaTitle;

    @Column(name = "meta_description", columnDefinition = "TEXT")
    private String metaDescription;

    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Builder.Default
    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ProductVariantEntity> variants = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ProductImageEntity> images = new HashSet<>();

    @Column(length = 100)
    private String brand;

    @Column(length = 100)
    private String material;

    @Column(length = 50)
    private String dimensions;

    @Column(precision = 10, scale = 3)
    private BigDecimal weight;

    @ElementCollection
    @CollectionTable(name = "product_tags", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    @Builder.Default
    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    @Builder.Default
    @Column(name = "sold_count", nullable = false)
    private Long soldCount = 0L;

    @Builder.Default
    @Column(name = "rating_avg", precision = 2, scale = 1, nullable = false)
    private BigDecimal ratingAvg = BigDecimal.ZERO;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @org.hibernate.annotations.ColumnTransformer(read = "embedding_vector::text", write = "?::vector")
    @Column(name = "embedding_vector", columnDefinition = "vector(1536)")
    private String embeddingVectorStr;

    /**
     * Dedicated style vector for FI-Agent module.
     *
     * Kept separate from generic embedding_vector so that stylist recommendations can
     * evolve its own dimensionality and retraining lifecycle without affecting other
     * embedding-based features.
     */
    @org.hibernate.annotations.ColumnTransformer(read = "style_vector::text", write = "?::vector")
    @Column(name = "style_vector", columnDefinition = "vector(384)")
    private String styleVectorStr;

    public void setEmbeddingVector(float[] vector) {
        if (vector == null) {
            this.embeddingVectorStr = null;
            return;
        }
        this.embeddingVectorStr = java.util.Arrays.toString(vector);
    }

    public float[] getEmbeddingVector() {
        if (embeddingVectorStr == null)
            return null;
        String[] parts = embeddingVectorStr.replace("[", "").replace("]", "").split(",");
        float[] res = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            res[i] = Float.parseFloat(parts[i].trim());
        }
        return res;
    }

    public void setStyleVector(float[] vector) {
        if (vector == null) {
            this.styleVectorStr = null;
            return;
        }
        this.styleVectorStr = java.util.Arrays.toString(vector);
    }

    public float[] getStyleVector() {
        if (styleVectorStr == null) {
            return null;
        }

        String[] parts = styleVectorStr.replace("[", "").replace("]", "").split(",");
        float[] res = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            res[i] = Float.parseFloat(parts[i].trim());
        }
        return res;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void incrementSoldCount(int quantity) {
        this.soldCount += quantity;
    }

    public void updateRatingAvg(BigDecimal newRating, int totalReviews) {
        this.ratingAvg = newRating;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper method to add variant
    public void addVariant(ProductVariantEntity variant) {
        if (variants == null) {
            variants = new HashSet<>();
        }
        variants.add(variant);
        variant.setProduct(this);
    }

    public void removeVariant(ProductVariantEntity variant) {
        if (variants != null) {
            variants.remove(variant);
            variant.setProduct(null);
        }
    }

    // Helper method to add image
    public void addImage(ProductImageEntity image) {
        if (images == null) {
            images = new HashSet<>();
        }
        images.add(image);
        image.setProduct(this);
    }

    public void removeImage(ProductImageEntity image) {
        if (images != null) {
            images.remove(image);
            image.setProduct(null);
        }
    }
}


