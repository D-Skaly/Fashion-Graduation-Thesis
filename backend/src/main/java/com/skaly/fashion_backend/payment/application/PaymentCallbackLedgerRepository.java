package com.skaly.fashion_backend.payment.application;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentCallbackLedgerRepository extends JpaRepository<PaymentCallbackLedger, UUID> {
    Optional<PaymentCallbackLedger> findByIdempotencyKey(String idempotencyKey);
}
