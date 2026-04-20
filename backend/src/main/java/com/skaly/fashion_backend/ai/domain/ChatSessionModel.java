package com.skaly.fashion_backend.ai.domain;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSessionModel {
    private UUID id;
    private UUID userId;
    private String title;
    private List<ChatMessageModel> messages;
    private LocalDateTime createdAt;
    private Boolean isActive;
}
