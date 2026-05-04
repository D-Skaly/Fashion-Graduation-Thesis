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
    
    /**
     * ChatMemory bean for storing conversation history.
     * Using default MessageWindowChatMemory implementation.
     */
    @Bean
    public ChatMemory chatMemory() {
        return ChatMemory.MAX_MESSAGES;
    }
    
    /**
     * MessageChatMemoryAdvisor for maintaining conversation context.
     */
    @Bean
    public MessageChatMemoryAdvisor messageChatMemoryAdvisor(ChatMemory chatMemory) {
        return new MessageChatMemoryAdvisor(chatMemory);
    }
}
