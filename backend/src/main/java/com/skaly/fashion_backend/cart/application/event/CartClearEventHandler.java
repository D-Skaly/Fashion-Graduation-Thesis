package com.skaly.fashion_backend.cart.application.event;

import com.skaly.fashion_backend.cart.application.CartRepository;
import com.skaly.fashion_backend.order.application.event.ClearCartRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class CartClearEventHandler {

    private final CartRepository cartRepository;

    @EventListener
    @Transactional
    public void handleClearCartRequested(ClearCartRequestedEvent event) {
        log.debug("Handling clear cart request for user: {}", event.userId());

        cartRepository.findByUserId(event.userId())
                .ifPresent(cart -> {
                    cart.clearItems();
                    cart.setCouponCode(null);
                    cart.setDiscountAmount(BigDecimal.ZERO);
                    cartRepository.save(cart);
                    log.info("Cart cleared for user: {}", event.userId());
                });
    }
}
