package com.skaly.fashion_backend.coupon.application.event;

import java.math.BigDecimal;
import java.util.UUID;

public record CouponValidationRequestedEvent(
    UUID cartId,
    String couponCode,
    BigDecimal subtotal
) {}
