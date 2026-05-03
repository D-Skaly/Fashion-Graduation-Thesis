package com.skaly.fashion_backend.common.domain;

/**
 * Exception thrown when AI service is unavailable.
 * Used in ai module when AI_ASSISTANT_ENABLED=false or API key is missing.
 * Now in common/domain/ for use by GlobalExceptionHandler.
 */
public class AiServiceUnavailableException extends RuntimeException {
    
    public AiServiceUnavailableException(String message) {
        super(message);
    }
    
    public AiServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
