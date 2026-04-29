package com.skaly.fashion_backend.cart.application.event;

import com.skaly.fashion_backend.cart.application.CartService;
import com.skaly.fashion_backend.events.UserLoggedInEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CartMergeEventListener {

    private final CartService cartService;

    @Async
    @EventListener
    public void handleUserLoggedInEvent(UserLoggedInEvent event) {
        if (event.getGuestId() != null && !event.getGuestId().isBlank()) {
            log.info("Merging guest cart for user: {}, guestId: {}", event.getEmail(), event.getGuestId());
            try {
                cartService.mergeCart(event.getEmail(), event.getGuestId());
                log.info("Successfully merged cart for user: {}", event.getEmail());
            } catch (Exception e) {
                log.error("Failed to merge cart for user: {}", event.getEmail(), e);
            }
        }
    }
}
