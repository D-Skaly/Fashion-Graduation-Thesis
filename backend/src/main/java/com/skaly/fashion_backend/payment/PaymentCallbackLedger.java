package com.skaly.fashion_backend.payment;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment_callback_ledger", indexes = {
        @Index(name = "idx_payment_callback_idempotency_key", columnList = "idempotency_key", unique = true),
        @Index(name = "idx_payment_callback_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCallbackLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "lookup_transaction_id", nullable = false)
    private String lookupTransactionId;

    @Column(name = "settled_transaction_id")
    private String settledTransactionId;

    @Column(name = "callback_success", nullable = false)
    private boolean callbackSuccess;

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 400)
    private String idempotencyKey;

    @Column(name = "raw_payload", columnDefinition = "TEXT")
    private String rawPayload;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
