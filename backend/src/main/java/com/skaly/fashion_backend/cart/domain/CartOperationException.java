package com.skaly.fashion_backend.cart.domain;

import com.skaly.fashion_backend.common.domain.BusinessException;

/**
 * Thrown when cart operations fail (empty cart checkout, stock exceeded, etc.)
 */
public class CartOperationException extends BusinessException {

    public CartOperationException(String message) {
        super(message);
    }
}
