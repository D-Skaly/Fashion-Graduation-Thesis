package com.skaly.fashion_backend.common.domain;

/**
 * Exception thrown when a requested resource is not found.
 * Used across multiple modules (product, cart, coupon, wishlist, etc.)
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ResourceNotFoundException(String resourceType, String identifier) {
        super(resourceType + " not found: " + identifier);
    }
}
