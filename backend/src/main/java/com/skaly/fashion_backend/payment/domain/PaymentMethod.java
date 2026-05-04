package com.skaly.fashion_backend.payment.domain;

/**
 * Payment method enumeration.
 * Represents supported payment methods in the system.
 */
public enum PaymentMethod {
    CREDIT_CARD("Credit Card", "Payment via credit card"),
    DEBIT_CARD("Debit Card", "Payment via debit card"),
    PAYPAL("PayPal", "Payment via PayPal account"),
    VNPAY("VNPay", "Payment via VNPay gateway"),
    MOMO("MoMo", "Payment via MoMo wallet"),
    COD("Cash on Delivery", "Cash payment upon delivery"),
    BANK_TRANSFER("Bank Transfer", "Direct bank transfer");

    private final String displayName;
    private final String description;

    PaymentMethod(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isOnlinePayment() {
        return this == CREDIT_CARD || this == DEBIT_CARD || this == PAYPAL 
               || this == VNPAY || this == MOMO || this == BANK_TRANSFER;
    }

    public boolean isCOD() {
        return this == COD;
    }
}
