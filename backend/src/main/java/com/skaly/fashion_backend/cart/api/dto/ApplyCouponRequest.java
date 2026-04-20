package com.skaly.fashion_backend.cart.api.dto;
import jakarta.validation.constraints.NotBlank;
public record ApplyCouponRequest(@NotBlank(message = "Coupon code is required") String couponCode) {}
