package com.skaly.fashion_backend.coupon.domain.exception;

/**
 * Ngoại lệ miền coupon — không phụ thuộc framework.
 */
public class InvalidCouponException extends RuntimeException {

    public InvalidCouponException(String message) {
        super(message);
    }
}
