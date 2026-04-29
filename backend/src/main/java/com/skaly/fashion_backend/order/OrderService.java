package com.skaly.fashion_backend.order;

import com.skaly.fashion_backend.common.ResourceNotFoundException;
import com.skaly.fashion_backend.order.domain.entities.Order;
import com.skaly.fashion_backend.order.domain.entities.OrderItem;
import com.skaly.fashion_backend.order.domain.OrderPricingService;
import com.skaly.fashion_backend.order.infrastructure.persistence.entities.OrderStatusHistoryEntity;
import com.skaly.fashion_backend.order.infrastructure.persistence.entities.ShippingEntity;
import com.skaly.fashion_backend.product.interfaces.dto.ProductVariantInternalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Order query and lifecycle management service.
 * Place-order logic lives in {@link com.skaly.fashion_backend.order.application.PlaceOrderUseCase}.
 * Cross-module dependencies are resolved via gateway ports, not direct service imports.
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository statusHistoryRepository;
    private final ShippingRepository shippingRepository;
    private final OrderUserGateway orderUserGateway;
    private final OrderInventoryGateway orderInventoryGateway;
    private final OrderPricingService orderPricingService;
    private final OrderEventService orderEventService;

    @Transactional(readOnly = true)
    public List<OrderDto> getUserOrders(String userEmail) {
        UUID userId = orderUserGateway.getUserIdByEmail(userEmail);
        return orderRepository.findByUserId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderById(String userEmail, UUID orderId) {
        UUID userId = orderUserGateway.getUserIdByEmail(userEmail);
        Order order = getOrderByIdOrThrow(orderId);
        validateOrderOwnership(userId, order, "view");
        return mapToDto(order);
    }

    @Transactional
    public OrderDto cancelOrder(String userEmail, UUID orderId, String reason) {
        UUID userId = orderUserGateway.getUserIdByEmail(userEmail);
        Order order = getOrderByIdOrThrow(orderId);
        validateOrderOwnership(userId, order, "cancel");
        validateOrderCanBeCancelled(order);

        OrderStatus oldStatus = order.getStatus();
        order.cancel(reason);

        Order savedOrder = orderRepository.save(order);
        orderEventService.publishOrderStatusChanged(savedOrder, oldStatus);
        return mapToDto(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderStatusHistoryEntity> getOrderStatusHistory(UUID orderId) {
        return statusHistoryRepository.findByOrderIdOrderByCreatedAtDesc(orderId);
    }

    @Transactional(readOnly = true)
    public ShippingEntity getOrderShipping(UUID orderId) {
        return shippingRepository.findByOrderId(orderId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Shipping info not found for order: " + orderId));
    }

    @Transactional
    public void updateOrderStatus(UUID orderId, OrderStatus newStatus, String note) {
        Order order = getOrderByIdOrThrow(orderId);
        OrderStatus oldStatus = order.getStatus();
        order.setStatus(newStatus);

        Order savedOrder = orderRepository.save(order);
        orderEventService.publishOrderStatusChanged(savedOrder, oldStatus);
    }

    private Order getOrderByIdOrThrow(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
    }

    private void validateOrderOwnership(UUID userId, Order order, String action) {
        if (!order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Not authorized to " + action + " this order");
        }
    }

    private void validateOrderCanBeCancelled(Order order) {
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order is already cancelled");
        }
        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel order that has been shipped or completed");
        }
    }

    private OrderDto mapToDto(Order order) {
        List<OrderItemDto> itemDtos = order.getItems().stream()
                .map(item -> {
                    ProductVariantInternalResponse variant = orderInventoryGateway
                            .getProductVariant(item.getProductVariantId());
                    BigDecimal subtotal = item.getSnapshotPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()));
                    return new OrderItemDto(
                            item.getId(),
                            variant.productName(),
                            variant.size(),
                            variant.color(),
                            item.getQuantity(),
                            item.getSnapshotPrice(),
                            subtotal);
                })
                .collect(Collectors.toList());

        return new OrderDto(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getShippingAddress(),
                itemDtos,
                order.getCreatedAt());
    }
}
