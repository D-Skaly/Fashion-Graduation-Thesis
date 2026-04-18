package com.skaly.fashion_backend.order;

import com.skaly.fashion_backend.cart.CartDto;
import com.skaly.fashion_backend.cart.CartItemDto;
import com.skaly.fashion_backend.cart.CartService;
import com.skaly.fashion_backend.common.ResourceNotFoundException;
import com.skaly.fashion_backend.product.ProductVariant;
import com.skaly.fashion_backend.user.User;
import com.skaly.fashion_backend.user.UserRepository;
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
    private final UserRepository userRepository;
    private final CartService cartService;
    private final OrderInventoryGateway orderInventoryGateway;
    private final OrderPricingService orderPricingService;
    private final OrderEventService orderEventService;

    @Transactional
    public OrderDto placeOrder(String userEmail, PlaceOrderRequest request) {
        User user = getUserByEmail(userEmail);
        CartDto cart = getValidatedCart(userEmail);

        Order order = Order.builder()
                .user(user)
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
        User user = getUserByEmail(userEmail);

        return orderRepository.findByUserId(user.getId()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderById(String userEmail, UUID orderId) {
        User user = getUserByEmail(userEmail);
        Order order = getOrderByIdOrThrow(orderId);
        validateOrderOwnership(user, order, "view");

        return mapToDto(order);
    }

    @Transactional
    public OrderDto cancelOrder(String userEmail, UUID orderId, String reason) {
        User user = getUserByEmail(userEmail);
        Order order = getOrderByIdOrThrow(orderId);
        validateOrderOwnership(user, order, "cancel");

        validateOrderCanBeCancelled(order);

        OrderStatus oldStatus = order.getStatus();
        order.cancel(reason);
        order.addStatusHistory(OrderStatus.CANCELLED, "Order cancelled: " + reason);

        Order savedOrder = orderRepository.save(order);
        orderEventService.publishOrderStatusChanged(savedOrder, oldStatus);
        return mapToDto(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderStatusHistory> getOrderStatusHistory(UUID orderId) {
        return statusHistoryRepository.findByOrderIdOrderByCreatedAtDesc(orderId);
    }

    @Transactional(readOnly = true)
    public Shipping getOrderShipping(UUID orderId) {
        return shippingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipping info not found for order: " + orderId));
    }

    @Transactional
    public Order updateOrderStatus(UUID orderId, OrderStatus newStatus, String note) {
        Order order = getOrderByIdOrThrow(orderId);

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(newStatus);
        order.addStatusHistory(newStatus, note);

        Order savedOrder = orderRepository.save(order);
        orderEventService.publishOrderStatusChanged(savedOrder, oldStatus);
        return savedOrder;
    }

    private User getUserByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
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

    private void validateOrderOwnership(User user, Order order, String action) {
        if (!order.getUser().getId().equals(user.getId())) {
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
            ProductVariant variant = orderInventoryGateway.getProductVariant(cartItem.productVariantId());
            BigDecimal unitPrice = orderPricingService.calculateUnitPrice(variant);
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productVariant(variant)
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
                    BigDecimal subtotal = item.getSnapshotPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()));
                    return new OrderItemDto(
                            item.getId(),
                            item.getProductVariant().getProduct().getName(),
                            item.getProductVariant().getSize(),
                            item.getProductVariant().getColor(),
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
