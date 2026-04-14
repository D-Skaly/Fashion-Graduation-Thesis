package com.skaly.fashion_backend.payment;

public enum PaymentMethod {
    COD,           // Cash on Delivery
    VNPAY,         // VNPay
    MOMO,          // Momo Wallet
    ZALOPAY,       // ZaloPay
    CREDIT_CARD,   // Stripe/Other credit card
    PAYPAL,        // PayPal
    BANK_TRANSFER  // Manual bank transfer
}
