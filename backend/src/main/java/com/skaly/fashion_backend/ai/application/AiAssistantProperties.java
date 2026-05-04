package com.skaly.fashion_backend.ai.application;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for AI Assistant feature.
 * Maps properties from application.yml with prefix "ai.assistant".
 */
@ConfigurationProperties(prefix = "ai.assistant")
public class AiAssistantProperties {

    private boolean enabled = true;
    private int maxMessageLength = 1000;
    private String model = "gemini-1.5-flash";
    private double temperature = 0.7;
    private int maxHistoryMessages = 10;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getMaxMessageLength() {
        return maxMessageLength;
    }

    public void setMaxMessageLength(int maxMessageLength) {
        this.maxMessageLength = maxMessageLength;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getMaxHistoryMessages() {
        return maxHistoryMessages;
    }

    public void setMaxHistoryMessages(int maxHistoryMessages) {
        this.maxHistoryMessages = maxHistoryMessages;
    }
}
