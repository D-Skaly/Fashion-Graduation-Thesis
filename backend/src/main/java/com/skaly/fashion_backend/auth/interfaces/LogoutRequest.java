package com.skaly.fashion_backend.auth.interfaces;

public record LogoutRequest(
    String accessToken,
    String refreshToken
) {}
