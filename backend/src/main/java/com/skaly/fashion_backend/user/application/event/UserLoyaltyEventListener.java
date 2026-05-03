package com.skaly.fashion_backend.user.application.event;

import com.skaly.fashion_backend.events.OrderStatusChangedEvent;
import com.skaly.fashion_backend.user.infrastructure.persistence.jpa.UserRepository;
import com.skaly.fashion_backend.user.domain.entities.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserLoyaltyEventListener {

    private final UserRepository userRepository;

    @Async
    @EventListener
    @Transactional
    public void handleOrderStatusChangedEvent(OrderStatusChangedEvent event) {
        // Only grant points when order is COMPLETED
        if ("COMPLETED".equals(event.getNewStatus())) {
            log.info("Order COMPLETED: Processing loyalty points for user {}", event.getUserId());
            
            userRepository.findById(event.getUserId()).ifPresent(user -> {
                // Calculation logic: 1 point for every 10,000 VND
                int pointsToAdd = event.getTotalAmount().divide(new java.math.BigDecimal(10000)).intValue();
                
                if (pointsToAdd > 0) {
                    user.addLoyaltyPoints(pointsToAdd);
                    userRepository.save(user);
                    log.info("Granted {} loyalty points to user {}. New total: {}", 
                            pointsToAdd, user.getEmail(), user.getLoyaltyPoints());
                }
            });
        }
    }
}
