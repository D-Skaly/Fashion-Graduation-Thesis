package com.skaly.fashion_backend.product.infrastructure.persistence.jpa.review;

import com.skaly.fashion_backend.product.infrastructure.persistence.jpa.ProductEntity;
import com.skaly.fashion_backend.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "reviews", indexes = {
    @Index(name = "idx_review_product_id", columnList = "product_id"),
    @Index(name = "idx_review_user_id", columnList = "user_id"),
    @Index(name = "idx_review_rating", columnList = "rating"),
    @Index(name = "idx_review_created_at", columnList = "createdAt")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(nullable = false)
    @Builder.Default
    private Integer rating = 5; // 1-5

    @Column(columnDefinition = "TEXT")
    private String comment;

    @ElementCollection
    @CollectionTable(name = "review_images", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "image_url")
    @Builder.Default
    private List<String> images = new ArrayList<>();

    @Column(name = "is_verified_purchase")
    @Builder.Default
    private Boolean isVerifiedPurchase = false;

    @Column(name = "helpful_count")
    @Builder.Default
    private Integer helpfulCount = 0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void incrementHelpfulCount() {
        this.helpfulCount++;
    }

    public void decrementHelpfulCount() {
        if (this.helpfulCount > 0) {
            this.helpfulCount--;
        }
    }
}


