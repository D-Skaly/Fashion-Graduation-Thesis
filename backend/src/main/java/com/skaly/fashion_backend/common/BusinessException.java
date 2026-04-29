package com.skaly.fashion_backend.common;

/**
 * Base exception for all business/domain-level errors.
 * Subclass this for domain-specific exceptions instead of using
 * generic IllegalStateException or IllegalArgumentException.
 */
public abstract class BusinessException extends RuntimeException {

    private final String errorCode;

    protected BusinessException(String message) {
        super(message);
        this.errorCode = getClass().getSimpleName();
    }

    protected BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    protected BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = getClass().getSimpleName();
    }

    public String getErrorCode() {
        return errorCode;
    }
}
