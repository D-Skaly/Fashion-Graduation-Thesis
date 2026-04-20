package com.skaly.fashion_backend.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skaly.fashion_backend.events.OrderCreatedEvent;
import com.skaly.fashion_backend.events.OrderStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxEventPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000) // Every 5 seconds
    @Transactional
    public void publishEvents() {
        List<OutboxEvent> events = outboxEventRepository.findByStatusOrderByCreatedAtAsc(OutboxEvent.OutboxStatus.PENDING);

        if (events.isEmpty()) {
            return;
        }

        log.info("Found {} pending events in outbox", events.size());

        for (OutboxEvent event : events) {
            try {
                Object domainEvent = deserializeEvent(event);
                if (domainEvent != null) {
                    eventPublisher.publishEvent(domainEvent);
                    event.setStatus(OutboxEvent.OutboxStatus.SENT);
                    outboxEventRepository.save(event);
                    log.info("Published and marked as SENT: {}/{}", event.getEventType(), event.getAggregateId());
                } else {
                    log.warn("Unknown event type: {}", event.getEventType());
                    event.setStatus(OutboxEvent.OutboxStatus.FAILED);
                    outboxEventRepository.save(event);
                }
            } catch (Exception e) {
                log.error("Failed to publish event: {}/{}", event.getEventType(), event.getAggregateId(), e);
                // We leave it as PENDING for retry, or could mark as FAILED depending on error type
            }
        }
    }

    private Object deserializeEvent(OutboxEvent event) throws Exception {
        return switch (event.getEventType()) {
            case "OrderCreated" -> objectMapper.readValue(event.getPayload(), OrderCreatedEvent.class);
            case "OrderStatusChanged" -> objectMapper.readValue(event.getPayload(), OrderStatusChangedEvent.class);
            // Add other event types here as needed
            default -> null;
        };
    }
}
