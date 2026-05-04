package com.skaly.fashion_backend.payment.domain;

/**
 * Payment status enumeration.
 * Represents the current state of a payment transaction.
 */
public enum PaymentStatus {
    PENDING("Pending", "Payment is awaiting processing"),
    PROCESSING("Processing", "Payment is being processed"),
    COMPLETED("Completed", "Payment completed successfully"),
    FAILED("Failed", "Payment failed"),
    CANCELLED("Cancelled", "Payment was cancelled by user"),
    REFUNDED("Refunded", "Payment was refunded");

    private final String displayName;
    private final String description;

    PaymentStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED || this == REFUNDED;
    }
}
