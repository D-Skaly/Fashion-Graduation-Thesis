package com.skaly.fashion_backend.order;

import com.skaly.fashion_backend.cart.api.dto.CartDto;
import com.skaly.fashion_backend.cart.api.dto.CartItemDto;
import com.skaly.fashion_backend.cart.application.CartService;
import com.skaly.fashion_backend.common.ResourceNotFoundException;
import com.skaly.fashion_backend.order.Order;
import com.skaly.fashion_backend.order.OrderItem;
import com.skaly.fashion_backend.order.OrderRepository;
import com.skaly.fashion_backend.order.OrderStatusHistoryEntity;

import com.skaly.fashion_backend.order.ShippingEntity;

import com.skaly.fashion_backend.product.interfaces.dto.ProductVariantInternalResponse;
import com.skaly.fashion_backend.user.api.dto.UserInternalResponse;
import com.skaly.fashion_backend.user.application.UserInternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository statusHistoryRepository;
    private final ShippingRepository shippingRepository;
    private final UserInternalService userInternalService;
    private final CartService cartService;
    private final OrderInventoryGateway orderInventoryGateway;
    private final OrderPricingService orderPricingService;
    private final OrderEventService orderEventService;

    @Transactional
    public OrderDto placeOrder(String userEmail, PlaceOrderRequest request) {
        UserInternalResponse user = userInternalService.getUserByEmail(userEmail);
        CartDto cart = getValidatedCart(userEmail);

        Order order = Order.builder()
                .userId(user.id())
                .status(OrderStatus.PENDING)
                .shippingAddress(request.shippingAddress())
                .build();

        BigDecimal totalAmount = addOrderItems(order, cart.items());
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);
        orderEventService.publishOrderCreated(savedOrder);

        // Clear cart after successful order creation
        cartService.clearCart(userEmail, null);

        return mapToDto(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getUserOrders(String userEmail) {
        UserInternalResponse user = userInternalService.getUserByEmail(userEmail);

        return orderRepository.findByUserId(user.id()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderById(String userEmail, UUID orderId) {
        UserInternalResponse user = userInternalService.getUserByEmail(userEmail);
        Order order = getOrderByIdOrThrow(orderId);
        validateOrderOwnership(user, order, "view");

        return mapToDto(order);
    }

    @Transactional
    public OrderDto cancelOrder(String userEmail, UUID orderId, String reason) {
        UserInternalResponse user = userInternalService.getUserByEmail(userEmail);
        Order order = getOrderByIdOrThrow(orderId);
        validateOrderOwnership(user, order, "cancel");

        validateOrderCanBeCancelled(order);

        OrderStatus oldStatus = order.getStatus();
        order.cancel(reason);
        // Note: Domain Order doesn't handle status history Entity directly. 
        // We'll handle it in the application service for now or move to domain.
        // For simplicity, let's keep the entity logic here for now but use domain order.

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
                .orElseThrow(() -> new ResourceNotFoundException("ShippingEntity info not found for order: " + orderId));
    }

    @Transactional
    public void updateOrderStatus(UUID orderId, OrderStatus newStatus, String note) {
        Order order = getOrderByIdOrThrow(orderId);

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(newStatus);

        Order savedOrder = orderRepository.save(order);
        orderEventService.publishOrderStatusChanged(savedOrder, oldStatus);
    }

    private CartDto getValidatedCart(String userEmail) {
        CartDto cart = cartService.getCart(userEmail, null);
        if (cart.items().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }
        return cart;
    }

    private Order getOrderByIdOrThrow(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
    }

    private void validateOrderOwnership(UserInternalResponse user, Order order, String action) {
        if (!order.getUserId().equals(user.id())) {
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

    private BigDecimal addOrderItems(Order order, List<CartItemDto> cartItems) {
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItemDto cartItem : cartItems) {
            ProductVariantInternalResponse variant = orderInventoryGateway.getProductVariant(cartItem.productVariantId());
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
                    ProductVariantInternalResponse variant = orderInventoryGateway.getProductVariant(item.getProductVariantId());
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

