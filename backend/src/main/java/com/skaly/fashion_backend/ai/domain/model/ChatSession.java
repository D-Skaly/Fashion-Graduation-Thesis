package com.skaly.fashion_backend.ai.domain.model;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model for AI chat sessions.
 * Lives in ai/domain/model/ (Clean Architecture).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSession {
    private UUID id;
    private UUID userId;
    private String sessionName;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Deactivate this session.
     */
    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Update the last updated timestamp.
     */
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
}
