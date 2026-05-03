package com.skaly.fashion_backend.ai.domain;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageModel {
    private UUID id;
    private String role;
    private String content;
    private LocalDateTime createdAt;

    // Custom builder to auto-generate ID and createdAt
    public static class ChatMessageModelBuilder {
        private UUID id = UUID.randomUUID();
        private LocalDateTime createdAt = LocalDateTime.now();
        
        public ChatMessageModelBuilder id(UUID id) {
            this.id = id;
            return this;
        }
        
        public ChatMessageModelBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        public ChatMessageModel build() {
            return new ChatMessageModel(id, role, content, createdAt);
        }
    }
}
