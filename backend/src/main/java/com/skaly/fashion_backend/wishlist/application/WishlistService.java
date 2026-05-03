package com.skaly.fashion_backend.wishlist.application;

import com.skaly.fashion_backend.common.domain.ResourceNotFoundException;
import com.skaly.fashion_backend.product.application.ProductMapper;
import com.skaly.fashion_backend.product.infrastructure.persistence.jpa.JpaProductRepository;
import com.skaly.fashion_backend.user.infrastructure.persistence.jpa.JpaUserRepository;
import com.skaly.fashion_backend.wishlist.domain.model.WishlistResponse;
import com.skaly.fashion_backend.wishlist.infrastructure.persistence.jpa.WishlistEntity;
import com.skaly.fashion_backend.wishlist.infrastructure.persistence.jpa.WishlistJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistJpaRepository wishlistJpaRepository;
    private final JpaUserRepository jpaUserRepository;
    private final JpaProductRepository jpaProductRepository;
    private final ProductMapper productMapper;

    @Transactional
    public WishlistResponse addToWishlist(UUID userId, UUID productId) {
        var user = jpaUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        var product = jpaProductRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductEntity not found with id: " + productId));

        if (wishlistJpaRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new IllegalStateException("ProductEntity already in wishlist");
        }

        WishlistEntity wishlist = WishlistEntity.builder()
                .user(user)
                .product(product)
                .build();

        WishlistEntity savedWishlist = wishlistJpaRepository.save(wishlist);
        return toWishlistResponse(savedWishlist);
    }

    @Transactional(readOnly = true)
    public Page<WishlistResponse> getUserWishlist(UUID userId, Pageable pageable) {
        return wishlistJpaRepository.findByUserId(userId, pageable)
                .map(this::toWishlistResponse);
    }

    @Transactional(readOnly = true)
    public boolean isInWishlist(UUID userId, UUID productId) {
        return wishlistJpaRepository.existsByUserIdAndProductId(userId, productId);
    }

    @Transactional
    public void removeFromWishlist(UUID userId, UUID productId) {
        if (!wishlistJpaRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new ResourceNotFoundException("ProductEntity not found in wishlist");
        }
        wishlistJpaRepository.deleteByUserIdAndProductId(userId, productId);
    }

    @Transactional
    public void clearWishlist(UUID userId) {
        var wishlists = wishlistJpaRepository.findByUserId(userId,
                        org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE))
                .getContent();
        wishlistJpaRepository.deleteAll(wishlists);
    }

    @Transactional
    public WishlistResponse moveToCart(UUID userId, UUID productId) {
        WishlistEntity wishlist = wishlistJpaRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductEntity not found in wishlist"));

        wishlistJpaRepository.delete(wishlist);

        return toWishlistResponse(wishlist);
    }

    private WishlistResponse toWishlistResponse(WishlistEntity wishlist) {
        return new WishlistResponse(
                wishlist.getId(),
                wishlist.getProduct().getId(),
                productMapper.toProductResponse(wishlist.getProduct()),
                wishlist.getCreatedAt()
        );
    }
}
