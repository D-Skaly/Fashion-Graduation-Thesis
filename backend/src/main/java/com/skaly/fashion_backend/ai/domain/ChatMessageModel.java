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
}
