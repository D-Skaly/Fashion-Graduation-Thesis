package com.skaly.fashion_backend.product.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewHelpfulVoteRepository extends JpaRepository<ReviewHelpfulVote, UUID> {

    Optional<ReviewHelpfulVote> findByUserIdAndReviewId(UUID userId, UUID reviewId);

    boolean existsByUserIdAndReviewId(UUID userId, UUID reviewId);

    void deleteByUserIdAndReviewId(UUID userId, UUID reviewId);
}
