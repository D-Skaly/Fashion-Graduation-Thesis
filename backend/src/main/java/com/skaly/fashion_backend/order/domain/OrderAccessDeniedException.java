package com.skaly.fashion_backend.order.domain;

import com.skaly.fashion_backend.common.BusinessException;

/**
 * Thrown when a user tries to access/modify an order they don't own.
 */
public class OrderAccessDeniedException extends BusinessException {

    public OrderAccessDeniedException(String action) {
        super("Not authorized to " + action + " this order");
    }
}
