package com.skaly.fashion_backend.auth.interfaces.api;

public record LogoutRequest(
    String accessToken,
    String refreshToken
) {}
