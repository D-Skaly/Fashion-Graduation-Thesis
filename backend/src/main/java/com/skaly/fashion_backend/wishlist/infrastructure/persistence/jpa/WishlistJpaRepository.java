package com.skaly.fashion_backend.wishlist.infrastructure.persistence.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WishlistJpaRepository extends JpaRepository<WishlistEntity, UUID> {

    Optional<WishlistEntity> findByUserIdAndProductId(UUID userId, UUID productId);

    @Query("SELECT w FROM WishlistEntity w WHERE w.user.id = :userId ORDER BY w.createdAt DESC")
    Page<WishlistEntity> findByUserId(@Param("userId") UUID userId, Pageable pageable);

    boolean existsByUserIdAndProductId(UUID userId, UUID productId);

    void deleteByUserIdAndProductId(UUID userId, UUID productId);
}
