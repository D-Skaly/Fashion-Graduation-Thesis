package com.skaly.fashion_backend.auth;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(
        @NotBlank(message = "Access token is required")
        String accessToken,
        
        String refreshToken
) {}
