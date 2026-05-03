package com.skaly.fashion_backend.auth.interfaces;

import java.time.Instant;

public record AuthenticationResponse(
    String accessToken,
    String refreshToken,
    long expiresIn,
    Instant refreshTokenExpiry,
    UserInfo user
) {
    public record UserInfo(
        String id,
        String email,
        String firstName,
        String lastName,
        String role,
        String avatarUrl
    ) {}
}
