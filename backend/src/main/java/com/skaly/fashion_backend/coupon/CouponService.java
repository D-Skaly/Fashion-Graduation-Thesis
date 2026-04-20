package com.skaly.fashion_backend.coupon;

import com.skaly.fashion_backend.common.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    @Transactional(readOnly = true)
    public BigDecimal calculateDiscount(String code, BigDecimal orderTotal) {
        if (code == null || code.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        Coupon coupon = couponRepository.findByCode(code)
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
        if (code == null || code.trim().isEmpty())
            return;

        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));
        coupon.incrementUsedCount();
        couponRepository.save(coupon);
    }

    @Transactional(readOnly = true)
    public Coupon getCouponByCode(String code) {
        return couponRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));
    }
}
