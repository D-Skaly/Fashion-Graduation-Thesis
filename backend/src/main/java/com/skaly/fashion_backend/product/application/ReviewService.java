package com.skaly.fashion_backend.product.application;

import com.skaly.fashion_backend.product.infrastructure.persistence.jpa.JpaProductRepository;
import com.skaly.fashion_backend.product.infrastructure.persistence.jpa.ProductEntity;
import com.skaly.fashion_backend.product.infrastructure.persistence.jpa.review.Review;
import com.skaly.fashion_backend.product.infrastructure.persistence.jpa.review.ReviewHelpfulVote;
import com.skaly.fashion_backend.product.infrastructure.persistence.jpa.review.ReviewHelpfulVoteRepository;
import com.skaly.fashion_backend.product.infrastructure.persistence.jpa.review.ReviewRepository;
import com.skaly.fashion_backend.user.User;
import com.skaly.fashion_backend.user.UserEntity;
import com.skaly.fashion_backend.user.infrastructure.persistence.jpa.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final JpaProductRepository jpaProductRepository;
    private final JpaUserRepository jpaUserRepository;
    private final ReviewHelpfulVoteRepository helpfulVoteRepository;

    @Transactional(readOnly = true)
    public Page<Review> getProductReviews(UUID productId, Integer rating, Pageable pageable) {
        if (rating != null) {
            return reviewRepository.findByProductIdAndRatingOrderByCreatedAtDesc(productId, rating, pageable);
        }
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Review> getVerifiedReviews(UUID productId, Pageable pageable) {
        return reviewRepository.findVerifiedReviewsByProductId(productId, pageable);
    }

    @Transactional(readOnly = true)
    public List<Review> getUserReviews(UUID userId) {
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public Review getReview(UUID reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found: " + reviewId));
    }

    @Transactional(readOnly = true)
    public boolean hasUserReviewed(UUID userId, UUID productId) {
        return reviewRepository.existsByUserIdAndProductId(userId, productId);
    }

    @Transactional
    public Review createReview(User user, UUID productId, Integer rating, String comment, List<String> images, boolean isVerifiedPurchase) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        if (hasUserReviewed(user.getId(), productId)) {
            throw new IllegalArgumentException("User has already reviewed this product");
        }

        ProductEntity product = jpaProductRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        UserEntity userEntity = jpaUserRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + user.getId()));

        Review review = Review.builder()
                .user(userEntity)
                .product(product)
                .rating(rating)
                .comment(comment)
                .images(images != null ? images : List.of())
                .isVerifiedPurchase(isVerifiedPurchase)
                .build();

        Review savedReview = reviewRepository.save(review);

        // Update product rating average
        updateProductRating(product);

        return savedReview;
    }

    @Transactional
    public Review updateReview(UUID reviewId, User user, Integer rating, String comment, List<String> images) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found: " + reviewId));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Not authorized to update this review");
        }

        if (rating != null && (rating < 1 || rating > 5)) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        if (rating != null) {
            review.setRating(rating);
        }
        if (comment != null) {
            review.setComment(comment);
        }
        if (images != null) {
            review.setImages(images);
        }

        Review savedReview = reviewRepository.save(review);

        // Update product rating average
        updateProductRating(review.getProduct());

        return savedReview;
    }

    @Transactional
    public void deleteReview(UUID reviewId, User user) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found: " + reviewId));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Not authorized to delete this review");
        }

        ProductEntity product = review.getProduct();
        reviewRepository.delete(review);

        // Update product rating average
        updateProductRating(product);
    }

    @Transactional
    public void voteHelpful(UUID reviewId, UUID userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found: " + reviewId));

        if (helpfulVoteRepository.existsByUserIdAndReviewId(userId, reviewId)) {
            throw new IllegalArgumentException("User has already voted this review as helpful");
        }

        UserEntity userEntity = jpaUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        ReviewHelpfulVote vote = ReviewHelpfulVote.builder()
                .user(userEntity)
                .review(review)
                .build();
        helpfulVoteRepository.save(vote);

        review.incrementHelpfulCount();
        reviewRepository.save(review);
    }

    @Transactional
    public void removeHelpfulVote(UUID reviewId, UUID userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found: " + reviewId));

        if (!helpfulVoteRepository.existsByUserIdAndReviewId(userId, reviewId)) {
            throw new IllegalArgumentException("User has not voted this review as helpful");
        }

        helpfulVoteRepository.deleteByUserIdAndReviewId(userId, reviewId);
        review.decrementHelpfulCount();
        reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public boolean hasUserVotedHelpful(UUID reviewId, UUID userId) {
        return helpfulVoteRepository.existsByUserIdAndReviewId(userId, reviewId);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getReviewStats(UUID productId) {
        long totalReviews = reviewRepository.countByProductId(productId);
        Double averageRating = reviewRepository.calculateAverageRating(productId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalReviews", totalReviews);
        stats.put("averageRating", averageRating != null ?
                BigDecimal.valueOf(averageRating).setScale(1, RoundingMode.HALF_UP) : BigDecimal.ZERO);

        // Rating distribution
        Map<Integer, Long> distribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            distribution.put(i, 0L);
        }

        List<Object[]> results = reviewRepository.getRatingDistribution(productId);
        for (Object[] result : results) {
            Integer rating = (Integer) result[0];
            Long count = (Long) result[1];
            distribution.put(rating, count);
        }
        stats.put("ratingDistribution", distribution);

        return stats;
    }

    private void updateProductRating(ProductEntity product) {
        Double avgRating = reviewRepository.calculateAverageRating(product.getId());
        long totalReviews = reviewRepository.countByProductId(product.getId());

        BigDecimal newRating = avgRating != null ?
                BigDecimal.valueOf(avgRating).setScale(1, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        product.updateRatingAvg(newRating, (int) totalReviews);
        jpaProductRepository.save(product);

        log.info("Updated product {} rating to {} ({} reviews)", product.getId(), newRating, totalReviews);
    }
}

