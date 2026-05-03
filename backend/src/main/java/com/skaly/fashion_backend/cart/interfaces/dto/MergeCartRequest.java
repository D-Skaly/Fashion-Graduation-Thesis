package com.skaly.fashion_backend.cart.interfaces.dto;
import jakarta.validation.constraints.NotBlank;
public record MergeCartRequest(@NotBlank(message = "Guest ID is required for merging cart") String guestId) {}
