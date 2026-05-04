package com.skaly.fashion_backend.ai.infrastructure;

import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for ChatMemory and Advisors.
 * Required for RAG pattern with ChatClient as per docs/ai-integration.md.
 */
@Configuration
public class ChatMemoryConfig {
    
    @Bean
    public ChatMemory chatMemory() {
        return new ChatMemory() {
            private final java.util.Map<String, java.util.List<org.springframework.ai.chat.messages.Message>> memory = new java.util.concurrent.ConcurrentHashMap<>();
            @Override
            public void add(String id, java.util.List<org.springframework.ai.chat.messages.Message> messages) {
                memory.put(id, messages);
            }
            @Override
            public java.util.List<org.springframework.ai.chat.messages.Message> get(String id) {
                return memory.getOrDefault(id, java.util.List.of());
            }
            @Override
            public void clear(String id) {
                memory.remove(id);
            }
        };
    }
    
    @Bean
    public MessageChatMemoryAdvisor messageChatMemoryAdvisor(ChatMemory chatMemory) {
        return MessageChatMemoryAdvisor.builder(chatMemory).build();
    }
}
