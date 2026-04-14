package com.skaly.fashion_backend.product.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    Page<Review> findByProductIdOrderByCreatedAtDesc(UUID productId, Pageable pageable);

    Page<Review> findByProductIdAndRatingOrderByCreatedAtDesc(UUID productId, Integer rating, Pageable pageable);

    List<Review> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<Review> findByUserIdAndProductId(UUID userId, UUID productId);

    boolean existsByUserIdAndProductId(UUID userId, UUID productId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId")
    long countByProductId(@Param("productId") UUID productId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double calculateAverageRating(@Param("productId") UUID productId);

    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.product.id = :productId GROUP BY r.rating")
    List<Object[]> getRatingDistribution(@Param("productId") UUID productId);

    @Query("SELECT r FROM Review r WHERE r.product.id = :productId AND r.isVerifiedPurchase = true ORDER BY r.createdAt DESC")
    Page<Review> findVerifiedReviewsByProductId(@Param("productId") UUID productId, Pageable pageable);
}
