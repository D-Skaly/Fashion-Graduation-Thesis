package com.skaly.fashion_backend.order.infrastructure.persistence.jpa;
import com.skaly.fashion_backend.order.infrastructure.persistence.entities.OrderItemEntity;
import com.skaly.fashion_backend.order.infrastructure.persistence.entities.OrderEntity;

import com.skaly.fashion_backend.order.domain.entities.Order;
import com.skaly.fashion_backend.order.domain.entities.OrderItem;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderPersistenceMapper {

    public OrderEntity toEntity(Order domain) {
        if (domain == null) return null;

        OrderEntity entity = OrderEntity.builder()
                .id(domain.getId())
                .orderNumber(domain.getOrderNumber())
                .userId(domain.getUserId())
                .subTotal(domain.getSubTotal())
                .taxAmount(domain.getTaxAmount())
                .shippingCost(domain.getShippingCost())
                .discountAmount(domain.getDiscountAmount())
                .discountCode(domain.getDiscountCode())
                .totalAmount(domain.getTotalAmount())
                .shippingAddress(domain.getShippingAddress())
                .notes(domain.getNotes())
                .cancelledAt(domain.getCancelledAt())
                .cancelledReason(domain.getCancelledReason())
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();

        if (domain.getItems() != null) {
            entity.setItems(domain.getItems().stream()
                    .map(item -> toItemEntity(item, entity))
                    .collect(Collectors.toList()));
        }

        return entity;
    }

    public Order toDomain(OrderEntity entity) {
        if (entity == null) return null;

        Order domain = Order.builder()
                .id(entity.getId())
                .orderNumber(entity.getOrderNumber())
                .userId(entity.getUserId())
                .subTotal(entity.getSubTotal())
                .taxAmount(entity.getTaxAmount())
                .shippingCost(entity.getShippingCost())
                .discountAmount(entity.getDiscountAmount())
                .discountCode(entity.getDiscountCode())
                .totalAmount(entity.getTotalAmount())
                .shippingAddress(entity.getShippingAddress())
                .notes(entity.getNotes())
                .cancelledAt(entity.getCancelledAt())
                .cancelledReason(entity.getCancelledReason())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();

        if (entity.getItems() != null) {
            domain.setItems(entity.getItems().stream()
                    .map(this::toItemDomain)
                    .collect(Collectors.toList()));
        }

        return domain;
    }

    private OrderItemEntity toItemEntity(OrderItem domain, OrderEntity orderEntity) {
        return OrderItemEntity.builder()
                .id(domain.getId())
                .order(orderEntity)
                .productVariantId(domain.getProductVariantId())
                .quantity(domain.getQuantity())
                .snapshotPrice(domain.getSnapshotPrice())
                .build();
    }

    private OrderItem toItemDomain(OrderItemEntity entity) {
        return OrderItem.builder()
                .id(entity.getId())
                .productVariantId(entity.getProductVariantId())
                .quantity(entity.getQuantity())
                .snapshotPrice(entity.getSnapshotPrice())
                .build();
    }
}

