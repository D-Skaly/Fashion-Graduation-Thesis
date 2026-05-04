package com.skaly.fashion_backend.auth.interfaces.dto;

/**
 * Request DTO for user logout.
 */
public class LogoutRequest {

    private String refreshToken;

    public LogoutRequest() {
    }

    public LogoutRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
