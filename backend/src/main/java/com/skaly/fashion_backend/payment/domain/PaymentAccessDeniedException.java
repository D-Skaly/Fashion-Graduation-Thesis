package com.skaly.fashion_backend.payment.domain;

import com.skaly.fashion_backend.common.domain.BusinessException;

public class PaymentAccessDeniedException extends BusinessException {

    public PaymentAccessDeniedException(String message) {
        super(message);
    }
}
