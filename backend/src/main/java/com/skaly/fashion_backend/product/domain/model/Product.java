package com.skaly.fashion_backend.product.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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
    
    private Boolean isActive = true;
    private Boolean isFeatured = false;
    
    private Category category;
    
    private Set<ProductVariant> variants = new HashSet<>();
    private Set<ProductImage> images = new HashSet<>();
    
    private String brand;
    private String material;
    private String dimensions;
    private BigDecimal weight;
    
    private Set<String> tags = new HashSet<>();
    
    private Long viewCount = 0L;
    private Long soldCount = 0L;
    private BigDecimal ratingAvg = BigDecimal.ZERO;
    
    private float[] embeddingVector;
    private float[] styleVector;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public Product() {
    }
    
    public Product(UUID id, String name, String sku, String slug, String description,
                   String careInstructions, String metaTitle, String metaDescription,
                   BigDecimal basePrice, Boolean isActive, Boolean isFeatured,
                   Category category, Set<ProductVariant> variants, Set<ProductImage> images,
                   String brand, String material, String dimensions, BigDecimal weight,
                   Set<String> tags, Long viewCount, Long soldCount, BigDecimal ratingAvg,
                   float[] embeddingVector, float[] styleVector, 
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.slug = slug;
        this.description = description;
        this.careInstructions = careInstructions;
        this.metaTitle = metaTitle;
        this.metaDescription = metaDescription;
        this.basePrice = basePrice;
        this.isActive = isActive != null ? isActive : true;
        this.isFeatured = isFeatured != null ? isFeatured : false;
        this.category = category;
        this.variants = variants != null ? variants : new HashSet<>();
        this.images = images != null ? images : new HashSet<>();
        this.brand = brand;
        this.material = material;
        this.dimensions = dimensions;
        this.weight = weight;
        this.tags = tags != null ? tags : new HashSet<>();
        this.viewCount = viewCount != null ? viewCount : 0L;
        this.soldCount = soldCount != null ? soldCount : 0L;
        this.ratingAvg = ratingAvg != null ? ratingAvg : BigDecimal.ZERO;
        this.embeddingVector = embeddingVector;
        this.styleVector = styleVector;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCareInstructions() { return careInstructions; }
    public void setCareInstructions(String careInstructions) { this.careInstructions = careInstructions; }
    
    public String getMetaTitle() { return metaTitle; }
    public void setMetaTitle(String metaTitle) { this.metaTitle = metaTitle; }
    
    public String getMetaDescription() { return metaDescription; }
    public void setMetaDescription(String metaDescription) { this.metaDescription = metaDescription; }
    
    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive != null ? isActive : true; }
    
    public Boolean getIsFeatured() { return isFeatured; }
    public void setIsFeatured(Boolean isFeatured) { this.isFeatured = isFeatured != null ? isFeatured : false; }
    
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    
    public Set<ProductVariant> getVariants() { return variants; }
    public void setVariants(Set<ProductVariant> variants) { this.variants = variants != null ? variants : new HashSet<>(); }
    
    public Set<ProductImage> getImages() { return images; }
    public void setImages(Set<ProductImage> images) { this.images = images != null ? images : new HashSet<>(); }
    
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    
    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }
    
    public String getDimensions() { return dimensions; }
    public void setDimensions(String dimensions) { this.dimensions = dimensions; }
    
    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
    
    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags != null ? tags : new HashSet<>(); }
    
    public Long getViewCount() { return viewCount; }
    public void setViewCount(Long viewCount) { this.viewCount = viewCount != null ? viewCount : 0L; }
    
    public Long getSoldCount() { return soldCount; }
    public void setSoldCount(Long soldCount) { this.soldCount = soldCount != null ? soldCount : 0L; }
    
    public BigDecimal getRatingAvg() { return ratingAvg; }
    public void setRatingAvg(BigDecimal ratingAvg) { this.ratingAvg = ratingAvg != null ? ratingAvg : BigDecimal.ZERO; }
    
    public float[] getEmbeddingVector() { return embeddingVector; }
    public void setEmbeddingVector(float[] embeddingVector) { this.embeddingVector = embeddingVector; }
    
    public float[] getStyleVector() { return styleVector; }
    public void setStyleVector(float[] styleVector) { this.styleVector = styleVector; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Domain methods
    public void addVariant(ProductVariant variant) {
        if (variants == null) {
            variants = new HashSet<>();
        }
        variants.add(variant);
    }
    
    public void addImage(ProductImage image) {
        if (images == null) {
            images = new HashSet<>();
        }
        images.add(image);
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private UUID id;
        private String name;
        private String sku;
        private String slug;
        private String description;
        private String careInstructions;
        private String metaTitle;
        private String metaDescription;
        private BigDecimal basePrice;
        private Boolean isActive = true;
        private Boolean isFeatured = false;
        private Category category;
        private Set<ProductVariant> variants = new HashSet<>();
        private Set<ProductImage> images = new HashSet<>();
        private String brand;
        private String material;
        private String dimensions;
        private BigDecimal weight;
        private Set<String> tags = new HashSet<>();
        private Long viewCount = 0L;
        private Long soldCount = 0L;
        private BigDecimal ratingAvg = BigDecimal.ZERO;
        private float[] embeddingVector;
        private float[] styleVector;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        public Builder id(UUID id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder sku(String sku) { this.sku = sku; return this; }
        public Builder slug(String slug) { this.slug = slug; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder careInstructions(String careInstructions) { this.careInstructions = careInstructions; return this; }
        public Builder metaTitle(String metaTitle) { this.metaTitle = metaTitle; return this; }
        public Builder metaDescription(String metaDescription) { this.metaDescription = metaDescription; return this; }
        public Builder basePrice(BigDecimal basePrice) { this.basePrice = basePrice; return this; }
        public Builder isActive(Boolean isActive) { this.isActive = isActive != null ? isActive : true; return this; }
        public Builder isFeatured(Boolean isFeatured) { this.isFeatured = isFeatured != null ? isFeatured : false; return this; }
        public Builder category(Category category) { this.category = category; return this; }
        public Builder variants(Set<ProductVariant> variants) { this.variants = variants != null ? variants : new HashSet<>(); return this; }
        public Builder images(Set<ProductImage> images) { this.images = images != null ? images : new HashSet<>(); return this; }
        public Builder brand(String brand) { this.brand = brand; return this; }
        public Builder material(String material) { this.material = material; return this; }
        public Builder dimensions(String dimensions) { this.dimensions = dimensions; return this; }
        public Builder weight(BigDecimal weight) { this.weight = weight; return this; }
        public Builder tags(Set<String> tags) { this.tags = tags != null ? tags : new HashSet<>(); return this; }
        public Builder viewCount(Long viewCount) { this.viewCount = viewCount != null ? viewCount : 0L; return this; }
        public Builder soldCount(Long soldCount) { this.soldCount = soldCount != null ? soldCount : 0L; return this; }
        public Builder ratingAvg(BigDecimal ratingAvg) { this.ratingAvg = ratingAvg != null ? ratingAvg : BigDecimal.ZERO; return this; }
        public Builder embeddingVector(float[] embeddingVector) { this.embeddingVector = embeddingVector; return this; }
        public Builder styleVector(float[] styleVector) { this.styleVector = styleVector; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        
        public Product build() {
            return new Product(id, name, sku, slug, description, careInstructions, metaTitle, metaDescription,
                               basePrice, isActive, isFeatured, category, variants, images, brand, material,
                               dimensions, weight, tags, viewCount, soldCount, ratingAvg, embeddingVector,
                               styleVector, createdAt, updatedAt);
        }
    }
}