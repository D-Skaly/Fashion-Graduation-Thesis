package com.skaly.fashion_backend.order.interfaces;

import com.skaly.fashion_backend.common.domain.ApiResponse;
import com.skaly.fashion_backend.order.application.OrderDto;
import com.skaly.fashion_backend.order.application.OrderService;
import com.skaly.fashion_backend.order.infrastructure.persistence.entities.OrderStatusHistoryEntity;
import com.skaly.fashion_backend.order.application.PlaceOrderRequest;
import com.skaly.fashion_backend.order.infrastructure.persistence.entities.ShippingEntity;
import com.skaly.fashion_backend.order.application.PlaceOrderUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
            @RequestBody PlaceOrderWithCartRequest request,
            Authentication authentication) {
        var userId = UUID.fromString(authentication.getName());
        var cartItems = request.cartItems().stream()
                .map(item -> new PlaceOrderUseCase.CartItemRequest(
                        item.productVariantId(),
                        item.quantity(),
                        item.snapshotPrice()))
                .toList();
        return ResponseEntity.ok(ApiResponse.success(
                placeOrderUseCase.execute(userId, authentication.getName(), cartItems, request.orderRequest())));
    }

    public record PlaceOrderWithCartRequest(
            PlaceOrderRequest orderRequest,
            List<CartItemRequest> cartItems
    ) {}

    public record CartItemRequest(
            UUID productVariantId,
            Integer quantity,
            BigDecimal snapshotPrice
    ) {}

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
