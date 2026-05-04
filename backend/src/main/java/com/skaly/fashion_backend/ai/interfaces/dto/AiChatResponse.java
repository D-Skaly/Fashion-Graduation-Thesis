package com.skaly.fashion_backend.ai.interfaces.dto;

/**
 * Response DTO for AI chat endpoint.
 */
public class AiChatResponse {

    private String message;
    private String sessionId;
    private Long timestamp;
    private Double relevancyScore;
    private Boolean factCheckPassed;

    public AiChatResponse() {
    }

    public AiChatResponse(String message, String sessionId) {
        this.message = message;
        this.sessionId = sessionId;
        this.timestamp = System.currentTimeMillis();
    }

    public AiChatResponse(String message, String sessionId, Double relevancyScore, Boolean factCheckPassed) {
        this.message = message;
        this.sessionId = sessionId;
        this.timestamp = System.currentTimeMillis();
        this.relevancyScore = relevancyScore;
        this.factCheckPassed = factCheckPassed;
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

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Double getRelevancyScore() {
        return relevancyScore;
    }

    public void setRelevancyScore(Double relevancyScore) {
        this.relevancyScore = relevancyScore;
    }

    public Boolean getFactCheckPassed() {
        return factCheckPassed;
    }

    public void setFactCheckPassed(Boolean factCheckPassed) {
        this.factCheckPassed = factCheckPassed;
    }
}
