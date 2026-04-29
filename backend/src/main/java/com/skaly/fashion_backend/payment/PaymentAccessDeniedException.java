package com.skaly.fashion_backend.payment;

import com.skaly.fashion_backend.common.BusinessException;

public class PaymentAccessDeniedException extends BusinessException {

    public PaymentAccessDeniedException(String message) {
        super(message);
    }
}
