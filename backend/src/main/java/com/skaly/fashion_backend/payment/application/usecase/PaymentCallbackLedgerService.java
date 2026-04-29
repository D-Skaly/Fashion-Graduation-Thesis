package com.skaly.fashion_backend.payment.application.usecase;

import com.skaly.fashion_backend.payment.PaymentCallbackLedger;
import com.skaly.fashion_backend.payment.PaymentCallbackLedgerRepository;
import com.skaly.fashion_backend.payment.PaymentMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCallbackLedgerService {

    private final PaymentCallbackLedgerRepository ledgerRepository;

    @Transactional
    public boolean registerIfFirstSeen(PaymentMethod method,
            String lookupTransactionId,
            String settledTransactionId,
            boolean callbackSuccess,
            String rawPayload) {
        String idempotencyKey = buildIdempotencyKey(method, lookupTransactionId, settledTransactionId, callbackSuccess);
        if (ledgerRepository.findByIdempotencyKey(idempotencyKey).isPresent()) {
            return false;
        }

        try {
            ledgerRepository.save(PaymentCallbackLedger.builder()
                    .paymentMethod(method)
                    .lookupTransactionId(lookupTransactionId)
                    .settledTransactionId(settledTransactionId)
                    .callbackSuccess(callbackSuccess)
                    .idempotencyKey(idempotencyKey)
                    .rawPayload(rawPayload)
                    .build());
            return true;
        } catch (DataIntegrityViolationException ex) {
            log.info("Duplicate callback ledger key detected: {}", idempotencyKey);
            return false;
        }
    }

    private String buildIdempotencyKey(PaymentMethod method,
            String lookupTransactionId,
            String settledTransactionId,
            boolean callbackSuccess) {
        return method + "|" + safe(lookupTransactionId) + "|" + safe(settledTransactionId) + "|" + callbackSuccess;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
