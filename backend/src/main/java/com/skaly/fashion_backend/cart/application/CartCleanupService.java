package com.skaly.fashion_backend.cart;

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

    // Run every day at 3 AM
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanupAbandonedGuestCarts() {
        log.info("Starting cleanup of abandoned guest carts...");

        // Carts older than 7 days
        LocalDateTime expiryDate = LocalDateTime.now().minusDays(7);
        List<Cart> abandonedCarts = cartRepository.findByGuestIdIsNotNullAndUpdatedAtBefore(expiryDate);

        if (!abandonedCarts.isEmpty()) {
            cartRepository.deleteAll(abandonedCarts);
            log.info("Cleaned up {} abandoned guest carts.", abandonedCarts.size());
        } else {
            log.info("No abandoned guest carts found to clean up.");
        }
    }
}
