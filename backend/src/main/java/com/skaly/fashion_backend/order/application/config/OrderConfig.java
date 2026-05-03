package com.skaly.fashion_backend.order.application.config;

import com.skaly.fashion_backend.order.domain.OrderPricingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Order module domain services.
 * Creates Spring beans for pure domain services.
 */
@Configuration
public class OrderConfig {

    @Bean
    public OrderPricingService orderPricingService() {
        return new OrderPricingService();
    }
}
