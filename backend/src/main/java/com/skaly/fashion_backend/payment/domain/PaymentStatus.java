package com.skaly.fashion_backend.payment.domain;

public enum PaymentStatus {
    PENDING,       // Waiting for payment
    PROCESSING,    // Payment in progress
    COMPLETED,     // Payment successful
    FAILED,        // Payment failed
    CANCELLED,     // Payment cancelled by user
    REFUNDED,      // Payment refunded
    PARTIALLY_REFUNDED  // Partial refund
}
