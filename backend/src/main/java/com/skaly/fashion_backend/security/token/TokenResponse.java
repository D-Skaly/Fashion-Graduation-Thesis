package com.skaly.fashion_backend.security.token;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record TokenResponse(
        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("refresh_token")
        String refreshToken,

        @JsonProperty("token_type")
        String tokenType,

        @JsonProperty("expires_in")
        long expiresIn,

        @JsonProperty("refresh_token_expires_at")
        Instant refreshTokenExpiresAt,

        @JsonProperty("user")
        UserInfo user
) {
    public TokenResponse {
        tokenType = "Bearer";
    }

    public TokenResponse(String accessToken, String refreshToken, long expiresIn,
                         Instant refreshTokenExpiresAt, UserInfo user) {
        this(accessToken, refreshToken, "Bearer", expiresIn, refreshTokenExpiresAt, user);
    }

    public record UserInfo(
            String id,
            String email,
            String firstName,
            String lastName,
            String role
    ) {}
}
