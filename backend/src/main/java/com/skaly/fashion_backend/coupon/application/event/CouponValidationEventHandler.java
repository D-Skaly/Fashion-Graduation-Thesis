package com.skaly.fashion_backend.coupon.application.event;

import com.skaly.fashion_backend.coupon.application.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponValidationEventHandler {

    private final CouponService couponService;
    private final ApplicationEventPublisher eventPublisher;

    @EventListener
    @Transactional(readOnly = true)
    public void handleCouponValidationRequested(CouponValidationRequestedEvent event) {
        log.debug("Handling coupon validation for cart: {}, code: {}",
                event.cartId(), event.couponCode());

        try {
            BigDecimal discount = couponService.calculateDiscount(
                    event.couponCode(),
                    event.subtotal()
            );

            var response = new CouponValidationResponseEvent(
                    event.cartId(),
                    event.couponCode(),
                    discount,
                    true,
                    null
            );
            eventPublisher.publishEvent(response);
        } catch (Exception e) {
            var response = new CouponValidationResponseEvent(
                    event.cartId(),
                    event.couponCode(),
                    BigDecimal.ZERO,
                    false,
                    e.getMessage()
            );
            eventPublisher.publishEvent(response);
        }
    }
}
