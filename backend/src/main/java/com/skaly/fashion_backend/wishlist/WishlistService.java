package com.skaly.fashion_backend.wishlist;

import com.skaly.fashion_backend.common.ResourceNotFoundException;
import com.skaly.fashion_backend.product.Product;
import com.skaly.fashion_backend.product.ProductMapper;
import com.skaly.fashion_backend.product.ProductRepository;
import com.skaly.fashion_backend.user.User;
import com.skaly.fashion_backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional
    public WishlistResponse addToWishlist(UUID userId, UUID productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new IllegalStateException("Product already in wishlist");
        }

        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .product(product)
                .build();

        Wishlist savedWishlist = wishlistRepository.save(wishlist);
        return toWishlistResponse(savedWishlist);
    }

    @Transactional(readOnly = true)
    public Page<WishlistResponse> getUserWishlist(UUID userId, Pageable pageable) {
        return wishlistRepository.findByUserId(userId, pageable)
                .map(this::toWishlistResponse);
    }

    @Transactional(readOnly = true)
    public boolean isInWishlist(UUID userId, UUID productId) {
        return wishlistRepository.existsByUserIdAndProductId(userId, productId);
    }

    @Transactional
    public void removeFromWishlist(UUID userId, UUID productId) {
        if (!wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new ResourceNotFoundException("Product not found in wishlist");
        }
        wishlistRepository.deleteByUserIdAndProductId(userId, productId);
    }

    @Transactional
    public void clearWishlist(UUID userId) {
        List<Wishlist> wishlists = wishlistRepository.findByUserId(userId, 
                org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE))
                .getContent();
        wishlistRepository.deleteAll(wishlists);
    }

    @Transactional
    public WishlistResponse moveToCart(UUID userId, UUID productId) {
        Wishlist wishlist = wishlistRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found in wishlist"));
        
        // Delete from wishlist after adding to cart logic should be handled in CartService
        wishlistRepository.delete(wishlist);
        
        return toWishlistResponse(wishlist);
    }

    private WishlistResponse toWishlistResponse(Wishlist wishlist) {
        return new WishlistResponse(
                wishlist.getId(),
                wishlist.getProduct().getId(),
                productMapper.toProductResponse(wishlist.getProduct()),
                wishlist.getCreatedAt()
        );
    }
}
