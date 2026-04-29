package com.skaly.fashion_backend.payment;

import com.skaly.fashion_backend.common.ApiResponse;
import com.skaly.fashion_backend.payment.application.usecase.HandlePaymentCallbackUseCase;
import com.skaly.fashion_backend.payment.application.usecase.InitiatePaymentUseCase;
import com.skaly.fashion_backend.payment.gateway.PaymentGateway;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final InitiatePaymentUseCase initiatePaymentUseCase;
    private final HandlePaymentCallbackUseCase handlePaymentCallbackUseCase;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentDto>> getPayment(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        Payment payment = paymentService.getPaymentById(id);
        // Check if user owns this payment
        if (!payment.getUserId().equals(userId)) {
            throw new PaymentAccessDeniedException("Not authorized to view this payment");
        }
        return ResponseEntity.ok(ApiResponse.success(mapToDto(payment)));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<PaymentDto>> getPaymentByOrder(
            @PathVariable UUID orderId,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        Payment payment = paymentService.getPaymentByOrderId(orderId);
        // Check if user owns this order
        if (!payment.getUserId().equals(userId)) {
            throw new PaymentAccessDeniedException("Not authorized to view this payment");
        }
        return ResponseEntity.ok(ApiResponse.success(mapToDto(payment)));
    }

    @GetMapping("/my-payments")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getMyPayments(
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        List<Payment> payments = paymentService.getPaymentsByUserId(userId);
        List<PaymentDto> response = payments.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // Admin endpoints
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getAllPayments(
            @RequestParam(required = false) PaymentStatus status) {
        List<Payment> payments = paymentService.getAllPayments(status);
        List<PaymentDto> response = payments.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/total-revenue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<java.math.BigDecimal>> getTotalRevenue() {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getTotalRevenue()));
    }

    private PaymentDto mapToDto(Payment payment) {
        return new PaymentDto(
                payment.getId(),
                payment.getOrderId(),
                payment.getTransactionId(),
                payment.getMethod(),
                payment.getStatus(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getPaidAt(),
                payment.getFailureReason(),
                payment.getCreatedAt());
    }

    public record PaymentDto(
            UUID id,
            UUID orderId,
            String transactionId,
            PaymentMethod method,
            PaymentStatus status,
            java.math.BigDecimal amount,
            String currency,
            java.time.LocalDateTime paidAt,
            String failureReason,
            java.time.LocalDateTime createdAt) {
    }

    @PostMapping("/init")
    public ResponseEntity<ApiResponse<PaymentGateway.PaymentResponse>> initPayment(
            @RequestBody PaymentInitRequest request,
            HttpServletRequest httpRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        var response = initiatePaymentUseCase.execute(
                request.orderId(),
                userId,
                request.returnUrl(),
                httpRequest.getRemoteAddr());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    public record PaymentInitRequest(
            UUID orderId,
            String returnUrl) {
    }

    // Payment Gateway Webhook Handlers

    @GetMapping("/vnpay/callback")
    public ResponseEntity<ApiResponse<String>> vnpayCallback(
            @RequestParam Map<String, String> params,
            HttpServletRequest request) {
        log.info("Received VNPay callback from IP: {}", request.getRemoteAddr());
        var result = handlePaymentCallbackUseCase.execute(PaymentMethod.VNPAY, params);
        if (result.httpStatus() >= 400) {
            return ResponseEntity.badRequest().body(ApiResponse.error(result.httpStatus(), result.message()));
        }
        return ResponseEntity.ok(ApiResponse.success(result.message()));
    }

    @GetMapping("/momo/callback")
    public ResponseEntity<ApiResponse<String>> momoCallback(
            @RequestParam Map<String, String> params,
            HttpServletRequest request) {
        log.info("Received Momo callback from IP: {}", request.getRemoteAddr());
        var result = handlePaymentCallbackUseCase.execute(PaymentMethod.MOMO, params);
        if (result.httpStatus() >= 400) {
            return ResponseEntity.badRequest().body(ApiResponse.error(result.httpStatus(), result.message()));
        }
        return ResponseEntity.ok(ApiResponse.success(result.message()));
    }
}
