package com.skaly.fashion_backend.cart.application;

import com.skaly.fashion_backend.cart.CartRepository;
import com.skaly.fashion_backend.cart.domain.entities.Cart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class CartCleanupService {

    private final CartRepository cartRepository;

    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanupAbandonedGuestCarts() {
        log.info("Starting cleanup of abandoned guest carts...");

        LocalDateTime expiryDate = LocalDateTime.now().minusDays(7);
        List<Cart> abandonedCarts = cartRepository.findAbandonedGuestCarts(expiryDate);

        if (!abandonedCarts.isEmpty()) {
            for (Cart cart : abandonedCarts) {
                cartRepository.delete(cart);
            }
            log.info("Cleaned up {} abandoned guest carts.", abandonedCarts.size());
        } else {
            log.info("No abandoned guest carts found to clean up.");
        }
    }
}
