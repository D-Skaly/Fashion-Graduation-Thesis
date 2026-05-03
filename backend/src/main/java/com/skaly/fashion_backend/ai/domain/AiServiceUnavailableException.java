package com.skaly.fashion_backend.ai.domain;

public class AiServiceUnavailableException extends RuntimeException {
    public AiServiceUnavailableException(String message) {
        super(message);
    }
}
