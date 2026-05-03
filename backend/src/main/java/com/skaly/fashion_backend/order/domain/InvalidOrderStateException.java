package com.skaly.fashion_backend.order.domain;

import com.skaly.fashion_backend.common.domain.BusinessException;

/**
 * Thrown when an order state transition is invalid (e.g., cancelling an already shipped order).
 */
public class InvalidOrderStateException extends BusinessException {

    public InvalidOrderStateException(String message) {
        super(message);
    }
}
