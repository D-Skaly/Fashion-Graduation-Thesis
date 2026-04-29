package com.skaly.fashion_backend.coupon.application.event;

import java.math.BigDecimal;
import java.util.UUID;

public record CouponValidationResponseEvent(
    UUID cartId,
    String couponCode,
    BigDecimal discountAmount,
    boolean valid,
    String errorMessage
) {}
