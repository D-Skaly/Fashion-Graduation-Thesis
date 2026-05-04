package com.skaly.fashion_backend.ai.interfaces.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for AI chat endpoint.
 */
public class AiChatRequest {

    @NotBlank(message = "Message must not be blank")
    private String message;

    private String sessionId;
    private Integer maxHistoryMessages;

    public AiChatRequest() {
    }

    public AiChatRequest(String message, String sessionId, Integer maxHistoryMessages) {
        this.message = message;
        this.sessionId = sessionId;
        this.maxHistoryMessages = maxHistoryMessages;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getMaxHistoryMessages() {
        return maxHistoryMessages;
    }

    public void setMaxHistoryMessages(Integer maxHistoryMessages) {
        this.maxHistoryMessages = maxHistoryMessages;
    }
}
