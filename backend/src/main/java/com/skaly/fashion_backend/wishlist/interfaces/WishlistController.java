package com.skaly.fashion_backend.wishlist.interfaces;

import com.skaly.fashion_backend.common.domain.ApiResponse;
import com.skaly.fashion_backend.wishlist.application.WishlistService;
import com.skaly.fashion_backend.wishlist.domain.model.WishlistResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping
    public ResponseEntity<ApiResponse<WishlistResponse>> addToWishlist(
            @RequestParam UUID userId,
            @RequestParam UUID productId) {
        WishlistResponse response = wishlistService.addToWishlist(userId, productId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<WishlistResponse>>> getUserWishlist(
            @RequestParam UUID userId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<WishlistResponse> response = wishlistService.getUserWishlist(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Boolean>> isInWishlist(
            @RequestParam UUID userId,
            @RequestParam UUID productId) {
        boolean isInWishlist = wishlistService.isInWishlist(userId, productId);
        return ResponseEntity.ok(ApiResponse.success(isInWishlist));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> removeFromWishlist(
            @RequestParam UUID userId,
            @RequestParam UUID productId) {
        wishlistService.removeFromWishlist(userId, productId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearWishlist(
            @RequestParam UUID userId) {
        wishlistService.clearWishlist(userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/move-to-cart")
    public ResponseEntity<ApiResponse<WishlistResponse>> moveToCart(
            @RequestParam UUID userId,
            @RequestParam UUID productId) {
        WishlistResponse response = wishlistService.moveToCart(userId, productId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
