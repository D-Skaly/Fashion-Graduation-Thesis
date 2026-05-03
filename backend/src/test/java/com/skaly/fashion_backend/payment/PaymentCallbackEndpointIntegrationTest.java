package com.skaly.fashion_backend.payment;

import com.skaly.fashion_backend.common.GlobalExceptionHandler;
import com.skaly.fashion_backend.payment.application.usecase.HandlePaymentCallbackUseCase;
import com.skaly.fashion_backend.payment.application.usecase.InitiatePaymentUseCase;
import com.skaly.fashion_backend.payment.domain.port.PaymentGatewayPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PaymentCallbackEndpointIntegrationTest {

        private MockMvc mockMvc;

        @BeforeEach
        void setUp() {
                // Mock the gateway to always return success
                PaymentGatewayPort mockGateway = mock(PaymentGatewayPort.class);
                when(mockGateway.paymentMethod()).thenReturn(PaymentMethod.VNPAY);
                when(mockGateway.processCallback(anyMap())).thenAnswer(invocation -> {
                        Map<String, String> params = invocation.getArgument(0);
                        String txnRef = params.get("vnp_TxnRef");
                        return new PaymentGatewayPort.CallbackVerificationResult(
                                        true,
                                        txnRef != null ? txnRef : "txn-123",
                                        "gateway-txn-456",
                                        "Success",
                                        "Payment successful");
                });

                PaymentGatewayPort mockMomoGateway = mock(PaymentGatewayPort.class);
                when(mockMomoGateway.paymentMethod()).thenReturn(PaymentMethod.MOMO);
                when(mockMomoGateway.processCallback(anyMap())).thenAnswer(invocation -> {
                        Map<String, String> params = invocation.getArgument(0);
                        String orderId = params.get("orderId");
                        return new PaymentGatewayPort.CallbackVerificationResult(
                                        true,
                                        orderId != null ? orderId : "order-123",
                                        "momo-txn-789",
                                        "Success",
                                        "Payment successful");
                });

                HandlePaymentCallbackUseCase handlePaymentCallbackUseCase = new HandlePaymentCallbackUseCase(
                                new PaymentServiceFake(),
                                new PaymentCallbackLedgerServiceFake(),
                                List.of(mockGateway, mockMomoGateway));

                PaymentController controller = new PaymentController(
                                new PaymentServiceFake(),
                                new InitiatePaymentUseCaseFake(),
                                handlePaymentCallbackUseCase);

                mockMvc = MockMvcBuilders.standaloneSetup(controller)
                                .setControllerAdvice(new GlobalExceptionHandler())
                                .build();
        }

        @Test
        void vnpayCallbackShouldReturnOk() throws Exception {
                mockMvc.perform(get("/api/v1/payments/vnpay/callback")
                                .param("vnp_TxnRef", "txn-ref-001")
                                .param("vnp_ResponseCode", "00"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200));
        }

        @Test
        void momoCallbackShouldReturnOk() throws Exception {
                mockMvc.perform(get("/api/v1/payments/momo/callback")
                                .param("orderId", "order-001")
                                .param("resultCode", "0"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200));
        }

        // Fake implementations for testing
        static class PaymentServiceFake extends com.skaly.fashion_backend.payment.PaymentService {
                public PaymentServiceFake() {
                        super(null, List.of());
                }

                @Override
                public com.skaly.fashion_backend.payment.Payment getPaymentByTransactionId(String transactionId) {
                        return com.skaly.fashion_backend.payment.Payment.builder()
                                        .id(java.util.UUID.randomUUID())
                                        .orderId(java.util.UUID.randomUUID())
                                        .userId(java.util.UUID.randomUUID())
                                        .method(com.skaly.fashion_backend.payment.PaymentMethod.VNPAY)
                                        .status(com.skaly.fashion_backend.payment.PaymentStatus.COMPLETED)
                                        .amount(java.math.BigDecimal.valueOf(100000))
                                        .currency("VND")
                                        .transactionId(transactionId)
                                        .build();
                }

                @Override
                public com.skaly.fashion_backend.payment.Payment processPayment(java.util.UUID paymentId,
                                String transactionId) {
                        return com.skaly.fashion_backend.payment.Payment.builder()
                                        .id(paymentId)
                                        .orderId(java.util.UUID.randomUUID())
                                        .userId(java.util.UUID.randomUUID())
                                        .method(com.skaly.fashion_backend.payment.PaymentMethod.VNPAY)
                                        .status(com.skaly.fashion_backend.payment.PaymentStatus.COMPLETED)
                                        .amount(java.math.BigDecimal.valueOf(100000))
                                        .currency("VND")
                                        .transactionId(transactionId)
                                        .build();
                }

                @Override
                public com.skaly.fashion_backend.payment.Payment failPayment(java.util.UUID paymentId, String reason) {
                        return com.skaly.fashion_backend.payment.Payment.builder()
                                        .id(paymentId)
                                        .orderId(java.util.UUID.randomUUID())
                                        .userId(java.util.UUID.randomUUID())
                                        .method(com.skaly.fashion_backend.payment.PaymentMethod.VNPAY)
                                        .status(com.skaly.fashion_backend.payment.PaymentStatus.FAILED)
                                        .amount(java.math.BigDecimal.valueOf(100000))
                                        .currency("VND")
                                        .failureReason(reason)
                                        .build();
                }
        }

        static class PaymentCallbackLedgerServiceFake
                        extends com.skaly.fashion_backend.payment.application.usecase.PaymentCallbackLedgerService {
                public PaymentCallbackLedgerServiceFake() {
                        super(null);
                }

                @Override
                public boolean registerIfFirstSeen(com.skaly.fashion_backend.payment.PaymentMethod method,
                                String lookupTransactionId,
                                String settledTransactionId,
                                boolean callbackSuccess,
                                String rawPayload) {
                        return true; // always first seen in test
                }
        }

        static class InitiatePaymentUseCaseFake
                        extends com.skaly.fashion_backend.payment.application.usecase.InitiatePaymentUseCase {
                public InitiatePaymentUseCaseFake() {
                        super(null);
                }
        }
}
