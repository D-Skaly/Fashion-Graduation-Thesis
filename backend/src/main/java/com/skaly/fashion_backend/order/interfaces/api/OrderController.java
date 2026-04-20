package com.skaly.fashion_backend.order.interfaces.api;

import com.skaly.fashion_backend.common.ApiResponse;
import com.skaly.fashion_backend.order.OrderDto;
import com.skaly.fashion_backend.order.OrderService;
import com.skaly.fashion_backend.order.OrderStatusHistoryEntity;
import com.skaly.fashion_backend.order.PlaceOrderRequest;
import com.skaly.fashion_backend.order.ShippingEntity;
import com.skaly.fashion_backend.order.application.PlaceOrderUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final PlaceOrderUseCase placeOrderUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderDto>> placeOrder(
            @RequestBody PlaceOrderRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(placeOrderUseCase.execute(authentication.getName(), request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderDto>>> getUserOrders(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getUserOrders(authentication.getName())));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDto>> getOrderDetail(
            @PathVariable UUID orderId,
            Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderById(authentication.getName(), orderId)));
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<OrderDto>> cancelOrder(
            @PathVariable UUID orderId,
            @RequestBody CancelOrderRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(
                orderService.cancelOrder(authentication.getName(), orderId, request.reason())));
    }

    @GetMapping("/{orderId}/status-history")
    public ResponseEntity<ApiResponse<List<OrderStatusHistoryEntity>>> getOrderStatusHistory(
            @PathVariable UUID orderId) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderStatusHistory(orderId)));
    }

    @GetMapping("/{orderId}/tracking")
    public ResponseEntity<ApiResponse<ShippingEntity>> getOrderTracking(
            @PathVariable UUID orderId) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderShipping(orderId)));
    }

    public record CancelOrderRequest(String reason) {
    }
}
