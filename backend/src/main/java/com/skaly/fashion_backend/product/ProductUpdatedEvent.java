package com.skaly.fashion_backend.product;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class ProductUpdatedEvent extends ApplicationEvent {
    private final UUID productId;

    public ProductUpdatedEvent(Object source, UUID productId) {
        super(source);
        this.productId = productId;
    }
}

