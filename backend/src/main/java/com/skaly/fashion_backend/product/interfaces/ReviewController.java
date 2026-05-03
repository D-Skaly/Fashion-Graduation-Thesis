package com.skaly.fashion_backend.product.interfaces;

import com.skaly.fashion_backend.product.application.ReviewService;
import com.skaly.fashion_backend.product.infrastructure.persistence.jpa.review.Review;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // Get product reviews with pagination
    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<Review>> getProductReviews(
            @PathVariable UUID productId,
            @RequestParam(required = false) @Min(1) @Max(5) Integer rating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(reviewService.getProductReviews(productId, rating, pageable));
    }

    // Get verified purchase reviews
    @GetMapping("/product/{productId}/verified")
    public ResponseEntity<Page<Review>> getVerifiedReviews(
            @PathVariable UUID productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(reviewService.getVerifiedReviews(productId, pageable));
    }

    // Get review statistics for a product
    @GetMapping("/product/{productId}/stats")
    public ResponseEntity<Map<String, Object>> getReviewStats(@PathVariable UUID productId) {
        return ResponseEntity.ok(reviewService.getReviewStats(productId));
    }

    // Get current user's reviews
    @GetMapping("/my-reviews")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Review>> getMyReviews(@AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        return ResponseEntity.ok(reviewService.getUserReviews(userId));
    }

    // Check if user has reviewed a product
    @GetMapping("/product/{productId}/has-reviewed")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Boolean>> hasUserReviewed(
            @PathVariable UUID productId,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        boolean hasReviewed = reviewService.hasUserReviewed(userId, productId);
        return ResponseEntity.ok(Map.of("hasReviewed", hasReviewed));
    }

    // Create a review
    @PostMapping("/product/{productId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Review> createReview(
            @PathVariable UUID productId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateReviewRequest request) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        Review review = reviewService.createReviewByUserId(
                userId,
                productId,
                request.rating(),
                request.comment(),
                request.images(),
                request.isVerifiedPurchase()
        );
        return ResponseEntity.ok(review);
    }

    // Update a review
    @PutMapping("/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Review> updateReview(
            @PathVariable UUID reviewId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateReviewRequest request) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        Review review = reviewService.updateReviewByUserId(
                reviewId,
                userId,
                request.rating(),
                request.comment(),
                request.images()
        );
        return ResponseEntity.ok(review);
    }

    // Delete a review
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteReview(
            @PathVariable UUID reviewId,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        reviewService.deleteReviewByUserId(reviewId, userId);
        return ResponseEntity.ok().build();
    }

    // Mark review as helpful
    @PostMapping("/{reviewId}/helpful")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> markHelpful(
            @PathVariable UUID reviewId,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        reviewService.voteHelpful(reviewId, userId);
        return ResponseEntity.ok().build();
    }

    // Remove helpful vote
    @DeleteMapping("/{reviewId}/helpful")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeHelpful(
            @PathVariable UUID reviewId,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        reviewService.removeHelpfulVote(reviewId, userId);
        return ResponseEntity.ok().build();
    }

    // Check if user has voted helpful
    @GetMapping("/{reviewId}/helpful/check")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Boolean>> hasUserVotedHelpful(
            @PathVariable UUID reviewId,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        boolean hasVoted = reviewService.hasUserVotedHelpful(reviewId, userId);
        return ResponseEntity.ok(Map.of("hasVoted", hasVoted));
    }

    // DTO Records
    public record CreateReviewRequest(
            @Min(1) @Max(5) Integer rating,
            String comment,
            List<String> images,
            Boolean isVerifiedPurchase
    ) {}

    public record UpdateReviewRequest(
            @Min(1) @Max(5) Integer rating,
            String comment,
            List<String> images
    ) {}
}
