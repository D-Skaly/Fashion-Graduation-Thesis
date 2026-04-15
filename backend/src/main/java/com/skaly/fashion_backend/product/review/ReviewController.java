package com.skaly.fashion_backend.product.review;

import com.skaly.fashion_backend.user.User;
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
    public ResponseEntity<List<Review>> getMyReviews(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(reviewService.getUserReviews(user.getId()));
    }

    // Check if user has reviewed a product
    @GetMapping("/product/{productId}/has-reviewed")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Boolean>> hasUserReviewed(
            @PathVariable UUID productId,
            @AuthenticationPrincipal User user) {

        boolean hasReviewed = reviewService.hasUserReviewed(user.getId(), productId);
        return ResponseEntity.ok(Map.of("hasReviewed", hasReviewed));
    }

    // Create a review
    @PostMapping("/product/{productId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Review> createReview(
            @PathVariable UUID productId,
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateReviewRequest request) {

        Review review = reviewService.createReview(
                user,
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
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateReviewRequest request) {

        Review review = reviewService.updateReview(
                reviewId,
                user,
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
            @AuthenticationPrincipal User user) {

        reviewService.deleteReview(reviewId, user);
        return ResponseEntity.ok().build();
    }

    // Mark review as helpful
    @PostMapping("/{reviewId}/helpful")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> markHelpful(
            @PathVariable UUID reviewId,
            @AuthenticationPrincipal User user) {
        reviewService.voteHelpful(reviewId, user.getId());
        return ResponseEntity.ok().build();
    }

    // Remove helpful vote
    @DeleteMapping("/{reviewId}/helpful")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeHelpful(
            @PathVariable UUID reviewId,
            @AuthenticationPrincipal User user) {
        reviewService.removeHelpfulVote(reviewId, user.getId());
        return ResponseEntity.ok().build();
    }

    // Check if user has voted helpful
    @GetMapping("/{reviewId}/helpful/check")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Boolean>> hasUserVotedHelpful(
            @PathVariable UUID reviewId,
            @AuthenticationPrincipal User user) {

        boolean hasVoted = reviewService.hasUserVotedHelpful(reviewId, user.getId());
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
