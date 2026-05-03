package com.skaly.fashion_backend.ai.domain.model;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model for chat messages in AI assistant conversations.
 * Lives in ai/domain/model/ (Clean Architecture).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private UUID id;
    private UUID sessionId;
    private String role;  // USER, ASSISTANT, SYSTEM"
    private String content;
    private LocalDateTime timestamp;
    private Boolean isActive;
}
