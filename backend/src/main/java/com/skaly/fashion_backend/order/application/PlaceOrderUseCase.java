package com.skaly.fashion_backend.order.application;

import com.skaly.fashion_backend.order.OrderDto;
import com.skaly.fashion_backend.order.OrderEventService;
import com.skaly.fashion_backend.order.OrderItemDto;
import com.skaly.fashion_backend.order.OrderInventoryGateway;
import com.skaly.fashion_backend.order.OrderStatus;
import com.skaly.fashion_backend.order.PlaceOrderRequest;
import com.skaly.fashion_backend.order.application.event.ClearCartRequestedEvent;
import com.skaly.fashion_backend.order.domain.OrderPricingService;
import com.skaly.fashion_backend.order.domain.entities.Order;
import com.skaly.fashion_backend.order.domain.entities.OrderItem;
import com.skaly.fashion_backend.order.OrderRepository;
import com.skaly.fashion_backend.product.interfaces.dto.ProductVariantInternalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaceOrderUseCase {

    private final OrderRepository orderRepository;
    private final OrderInventoryGateway orderInventoryGateway;
    private final OrderPricingService orderPricingService;
    private final OrderEventService orderEventService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public OrderDto execute(UUID userId, String userEmail, List<CartItemRequest> cartItems, PlaceOrderRequest request) {
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        Order order = Order.builder()
                .userId(userId)
                .status(OrderStatus.PENDING)
                .shippingAddress(request.shippingAddress())
                .build();

        BigDecimal totalAmount = addOrderItems(order, cartItems);
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);
        orderEventService.publishOrderCreated(savedOrder);

        // Publish event to clear cart (async)
        eventPublisher.publishEvent(new ClearCartRequestedEvent(userId, userEmail));

        return mapToDto(savedOrder);
    }

    public record CartItemRequest(
            UUID productVariantId,
            Integer quantity,
            BigDecimal snapshotPrice
    ) {}

    private BigDecimal addOrderItems(Order order, List<CartItemRequest> cartItems) {
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItemRequest cartItem : cartItems) {
            ProductVariantInternalResponse variant = orderInventoryGateway
                    .getProductVariant(cartItem.productVariantId());
            BigDecimal unitPrice = orderPricingService.calculateUnitPrice(variant);
            OrderItem orderItem = OrderItem.builder()
                    .productVariantId(variant.id())
                    .quantity(cartItem.quantity())
                    .snapshotPrice(unitPrice)
                    .build();

            order.addItem(orderItem);
            totalAmount = totalAmount.add(orderPricingService.calculateLineTotal(unitPrice, cartItem.quantity()));
        }

        return totalAmount;
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
