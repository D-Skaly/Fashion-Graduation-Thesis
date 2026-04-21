package com.skaly.fashion_backend.order.infrastructure.persistence.jpa;

import com.skaly.fashion_backend.order.OrderNoteRepository;
import com.skaly.fashion_backend.order.OrderRepository;
import com.skaly.fashion_backend.order.OrderStatusHistoryRepository;
import com.skaly.fashion_backend.order.ShippingRepository;
import com.skaly.fashion_backend.order.domain.entities.Order;
import com.skaly.fashion_backend.order.infrastructure.persistence.entities.OrderNoteEntity;
import com.skaly.fashion_backend.order.infrastructure.persistence.entities.OrderStatusHistoryEntity;
import com.skaly.fashion_backend.order.infrastructure.persistence.entities.OrderEntity;
import com.skaly.fashion_backend.order.infrastructure.persistence.entities.ShippingEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderPersistenceAdapter implements OrderRepository, OrderNoteRepository, OrderStatusHistoryRepository, ShippingRepository {

    private final JpaOrderRepository jpaOrderRepository;
    private final JpaOrderNoteRepository jpaOrderNoteRepository;
    private final JpaOrderStatusHistoryRepository jpaOrderStatusHistoryRepository;
    private final JpaShippingRepository jpaShippingRepository;
    private final OrderPersistenceMapper mapper;

    @Override
    public Order save(Order order) {
        OrderEntity entity = mapper.toEntity(order);
        OrderEntity savedEntity = jpaOrderRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return jpaOrderRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Order> findByUserId(UUID userId) {
        return jpaOrderRepository.findByUserId(userId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderNoteEntity> findOrderNotesByOrderIdOrderByCreatedAtDesc(UUID orderId) {
        return jpaOrderNoteRepository.findByOrderIdOrderByCreatedAtDesc(orderId);
    }

    @Override
    public OrderNoteEntity save(OrderNoteEntity note) {
        return jpaOrderNoteRepository.save(note);
    }

    @Override
    public List<OrderStatusHistoryEntity> findByOrderIdOrderByCreatedAtDesc(UUID orderId) {
        return jpaOrderStatusHistoryRepository.findByOrderIdOrderByCreatedAtDesc(orderId);
    }

    @Override
    public OrderStatusHistoryEntity save(OrderStatusHistoryEntity history) {
        return jpaOrderStatusHistoryRepository.save(history);
    }

    @Override
    public Optional<ShippingEntity> findByOrderId(UUID orderId) {
        return jpaShippingRepository.findByOrderId(orderId);
    }

    @Override
    public ShippingEntity save(ShippingEntity shipping) {
        return jpaShippingRepository.save(shipping);
    }
}
