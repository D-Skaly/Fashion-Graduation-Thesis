package com.skaly.fashion_backend.payment.domain;

/**
 * Enum representing payment status.
 * Used in Payment domain model and DTOs.
 */
public enum PaymentStatus {
    PENDING,      // Payment initiated, awaiting confirmation
    COMPLETED,   // Payment successfully processed
    FAILED,      // Payment failed
    CANCELLED,   // Payment cancelled by user
    REFUNDED     // Payment refunded
}
