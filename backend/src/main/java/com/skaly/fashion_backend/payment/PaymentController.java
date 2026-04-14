package com.skaly.fashion_backend.payment;

import com.skaly.fashion_backend.common.ApiResponse;
import com.skaly.fashion_backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user) {
        Payment payment = paymentService.getPaymentById(id);
        // Check if user owns this payment
        if (!payment.getOrder().getUser().getId().equals(user.getId())) {
            throw new SecurityException("Not authorized to view this payment");
        }
        return ResponseEntity.ok(ApiResponse.success(mapToResponse(payment)));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByOrder(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal User user) {
        Payment payment = paymentService.getPaymentByOrderId(orderId);
        // Check if user owns this order
        if (!payment.getOrder().getUser().getId().equals(user.getId())) {
            throw new SecurityException("Not authorized to view this payment");
        }
        return ResponseEntity.ok(ApiResponse.success(mapToResponse(payment)));
    }

    @GetMapping("/my-payments")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getMyPayments(
            @AuthenticationPrincipal User user) {
        List<Payment> payments = paymentRepository.findByUserId(user.getId());
        List<PaymentResponse> response = payments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // Admin endpoints
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getAllPayments(
            @RequestParam(required = false) PaymentStatus status) {
        List<Payment> payments;
        if (status != null) {
            payments = paymentService.getPaymentsByStatus(status);
        } else {
            payments = paymentRepository.findAll();
        }
        List<PaymentResponse> response = payments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/total-revenue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<java.math.BigDecimal>> getTotalRevenue() {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getTotalRevenue()));
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getTransactionId(),
                payment.getMethod(),
                payment.getStatus(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getPaidAt(),
                payment.getFailureReason(),
                payment.getCreatedAt()
        );
    }

    public record PaymentResponse(
            UUID id,
            UUID orderId,
            String transactionId,
            PaymentMethod method,
            PaymentStatus status,
            java.math.BigDecimal amount,
            String currency,
            java.time.LocalDateTime paidAt,
            String failureReason,
            java.time.LocalDateTime createdAt
    ) {}
}
