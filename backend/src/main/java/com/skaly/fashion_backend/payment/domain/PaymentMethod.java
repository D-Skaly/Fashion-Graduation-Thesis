package com.skaly.fashion_backend.payment.domain;

/**
 * Enum representing payment methods.
 * Used in Payment domain model and DTOs.
 */
public enum PaymentMethod {
    CASH,          // Cash on delivery
    CREDIT_CARD,  // Credit card payment
    DEBIT_CARD,   // Debit card payment
    BANK_TRANSFER, // Bank transfer
    E_WALLET,      // E-wallet (Momo, VNPay, etc.)
    OTHER          // Other payment methods
}
