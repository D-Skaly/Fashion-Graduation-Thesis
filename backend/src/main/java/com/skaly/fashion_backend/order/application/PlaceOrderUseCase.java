package com.skaly.fashion_backend.order.application;

import com.skaly.fashion_backend.cart.api.dto.CartDto;
import com.skaly.fashion_backend.cart.api.dto.CartItemDto;
import com.skaly.fashion_backend.cart.application.CartService;
import com.skaly.fashion_backend.order.OrderDto;
import com.skaly.fashion_backend.order.OrderEventService;
import com.skaly.fashion_backend.order.OrderItemDto;
import com.skaly.fashion_backend.order.OrderInventoryGateway;
import com.skaly.fashion_backend.order.OrderPricingService;
import com.skaly.fashion_backend.order.OrderStatus;
import com.skaly.fashion_backend.order.PlaceOrderRequest;
import com.skaly.fashion_backend.order.Order;
import com.skaly.fashion_backend.order.OrderItem;
import com.skaly.fashion_backend.order.OrderRepository;
import com.skaly.fashion_backend.product.interfaces.dto.ProductVariantInternalResponse;
import com.skaly.fashion_backend.user.api.dto.UserInternalResponse;
import com.skaly.fashion_backend.user.application.UserInternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaceOrderUseCase {

    private final OrderRepository orderRepository;
    private final UserInternalService userInternalService;
    private final CartService cartService;
    private final OrderInventoryGateway orderInventoryGateway;
    private final OrderPricingService orderPricingService;
    private final OrderEventService orderEventService;

    @Transactional
    public OrderDto execute(String userEmail, PlaceOrderRequest request) {
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

    private CartDto getValidatedCart(String userEmail) {
        CartDto cart = cartService.getCart(userEmail, null);
        if (cart.items().isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }
        return cart;
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
