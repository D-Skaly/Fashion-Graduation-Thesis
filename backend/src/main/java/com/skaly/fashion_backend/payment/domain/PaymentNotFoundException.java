package com.skaly.fashion_backend.payment.domain;

import com.skaly.fashion_backend.common.domain.BusinessException;

/**
 * Thrown when a payment is not found by id, orderId, or transactionId.
 */
public class PaymentNotFoundException extends BusinessException {

    public PaymentNotFoundException(String message) {
        super(message);
    }
}
