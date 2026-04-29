package com.skaly.fashion_backend.payment;

import com.skaly.fashion_backend.common.BusinessException;

/**
 * Thrown when a payment is not found by id, orderId, or transactionId.
 */
public class PaymentNotFoundException extends BusinessException {

    public PaymentNotFoundException(String message) {
        super(message);
    }
}
