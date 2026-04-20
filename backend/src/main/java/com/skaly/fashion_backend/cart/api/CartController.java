package com.skaly.fashion_backend.cart.api;

import com.skaly.fashion_backend.cart.api.dto.*;
import com.skaly.fashion_backend.cart.application.CartService;

import com.skaly.fashion_backend.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    private String getUserEmail(Authentication authentication) {
        return (authentication != null && authentication.isAuthenticated()) ? authentication.getName() : null;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CartDto>> getCart(
            Authentication authentication,
            @RequestHeader(value = "X-Guest-Id", required = false) String guestId) {
        return ResponseEntity.ok(ApiResponse.success(cartService.getCart(getUserEmail(authentication), guestId)));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartDto>> addToCart(
            @RequestBody AddToCartRequest request,
            Authentication authentication,
            @RequestHeader(value = "X-Guest-Id", required = false) String guestId) {
        return ResponseEntity
                .ok(ApiResponse.success(cartService.addToCart(getUserEmail(authentication), guestId, request)));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<CartDto>> updateCartItem(
            @RequestBody UpdateCartRequest request,
            Authentication authentication,
            @RequestHeader(value = "X-Guest-Id", required = false) String guestId) {
        return ResponseEntity
                .ok(ApiResponse.success(cartService.updateCartItem(getUserEmail(authentication), guestId, request)));
    }

    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<ApiResponse<CartDto>> removeCartItem(
            @PathVariable UUID itemId,
            Authentication authentication,
            @RequestHeader(value = "X-Guest-Id", required = false) String guestId) {
        return ResponseEntity
                .ok(ApiResponse.success(cartService.removeCartItem(getUserEmail(authentication), guestId, itemId)));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart(
            Authentication authentication,
            @RequestHeader(value = "X-Guest-Id", required = false) String guestId) {
        cartService.clearCart(getUserEmail(authentication), guestId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/merge")
    public ResponseEntity<ApiResponse<CartDto>> mergeCart(
            @RequestBody MergeCartRequest request,
            Authentication authentication) {
        String email = getUserEmail(authentication);
        if (email == null) {
            return ResponseEntity.status(401).body(ApiResponse.error(401, "Authentication required for merging"));
        }
        return ResponseEntity.ok(ApiResponse.success(cartService.mergeCart(email, request.guestId())));
    }

    @PostMapping("/coupon")
    public ResponseEntity<ApiResponse<CartDto>> applyCoupon(
            @RequestBody ApplyCouponRequest request,
            Authentication authentication,
            @RequestHeader(value = "X-Guest-Id", required = false) String guestId) {
        return ResponseEntity.ok(ApiResponse
                .success(cartService.applyCoupon(getUserEmail(authentication), guestId, request.couponCode())));
    }
}
