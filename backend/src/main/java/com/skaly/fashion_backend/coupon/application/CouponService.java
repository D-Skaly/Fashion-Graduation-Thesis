package com.skaly.fashion_backend.coupon.application;

import com.skaly.fashion_backend.common.ResourceNotFoundException;
import com.skaly.fashion_backend.coupon.domain.exception.InvalidCouponException;
import com.skaly.fashion_backend.coupon.infrastructure.persistence.jpa.CouponEntity;
import com.skaly.fashion_backend.coupon.infrastructure.persistence.jpa.CouponJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponJpaRepository couponJpaRepository;

    @Transactional(readOnly = true)
    public BigDecimal calculateDiscount(String code, BigDecimal orderTotal) {
        if (code == null || code.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        CouponEntity coupon = couponJpaRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));

        if (!coupon.isValid()) {
            throw new InvalidCouponException("Coupon is not valid or has expired");
        }

        if (!coupon.isApplicable(orderTotal)) {
            throw new InvalidCouponException("OrderEntity total does not meet the minimum requirement for this coupon");
        }

        return coupon.calculateDiscount(orderTotal);
    }

    @Transactional
    public void incrementUsage(String code) {
        if (code == null || code.trim().isEmpty()) {
            return;
        }

        CouponEntity coupon = couponJpaRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));
        coupon.incrementUsedCount();
        couponJpaRepository.save(coupon);
    }

    @Transactional(readOnly = true)
    public CouponEntity getCouponByCode(String code) {
        return couponJpaRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));
    }
}
