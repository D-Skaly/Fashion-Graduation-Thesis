package com.skaly.fashion_backend.email.infrastructure.event;

import com.skaly.fashion_backend.email.application.EmailService;
import com.skaly.fashion_backend.events.OrderCreatedEvent;
import com.skaly.fashion_backend.user.infrastructure.persistence.jpa.UserRepository;
import com.skaly.fashion_backend.user.domain.entities.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEmailEventListener {

    private final EmailService emailService;
    private final UserRepository userRepository;

    @Async
    @EventListener
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Handling OrderCreatedEvent for order: {}", event.getOrderNumber());
        
        userRepository.findById(event.getUserId()).ifPresent(user -> {
            emailService.sendOrderConfirmationEmail(
                    user.getEmail(),
                    event.getOrderNumber(),
                    user.getFirstName() + " " + user.getLastName(),
                    event.getTotalAmount().doubleValue(),
                    "/account/orders/" + event.getOrderId()
            );
        });
    }
}
