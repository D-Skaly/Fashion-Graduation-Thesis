package com.skaly.fashion_backend.common.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Nền tảng WebSocket/STOMP cho kênh chat thời gian thực (Giai đoạn 1).
 * <p>
 * Client (Next.js) kết nối STOMP tới {@code /ws/chat}, publish lên {@code /app/chat.send}, subscribe {@code /topic/chat}.
 * Phần xử lý tin nhắn (controller {@code @MessageMapping}) có thể bổ sung sau khi đồng bộ với JWT/session.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/chat")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}