package com.skaly.fashion_backend.order.infrastructure.persistence.jpa;

import com.skaly.fashion_backend.order.application.OrderNoteRepository;
import com.skaly.fashion_backend.order.application.OrderRepository;
import com.skaly.fashion_backend.order.application.OrderStatusHistoryRepository;
import com.skaly.fashion_backend.order.application.ShippingRepository;
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
import java.util.concurrent.StructuredTaskScope;
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
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var future = scope.fork(() -> {
                OrderEntity entity = mapper.toEntity(order);
                OrderEntity savedEntity = jpaOrderRepository.save(entity);
                return mapper.toDomain(savedEntity);
            });
            scope.join();
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save order", e);
        }
    }

    @Override
    public Optional<Order> findById(UUID id) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var future = scope.fork(() -> jpaOrderRepository.findById(id).map(mapper::toDomain));
            scope.join();
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to find order by id", e);
        }
    }

    @Override
    public List<Order> findByUserId(UUID userId) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var future = scope.fork(() -> jpaOrderRepository.findByUserId(userId).stream()
                    .map(mapper::toDomain)
                    .collect(Collectors.toList()));
            scope.join();
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to find orders by user id", e);
        }
    }

    @Override
    public List<OrderNoteEntity> findOrderNotesByOrderIdOrderByCreatedAtDesc(UUID orderId) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var future = scope.fork(() -> jpaOrderNoteRepository.findByOrderIdOrderByCreatedAtDesc(orderId));
            scope.join();
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to find order notes", e);
        }
    }

    @Override
    public OrderNoteEntity save(OrderNoteEntity note) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var future = scope.fork(() -> jpaOrderNoteRepository.save(note));
            scope.join();
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save order note", e);
        }
    }

    @Override
    public List<OrderStatusHistoryEntity> findByOrderIdOrderByCreatedAtDesc(UUID orderId) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var future = scope.fork(() -> jpaOrderStatusHistoryRepository.findByOrderIdOrderByCreatedAtDesc(orderId));
            scope.join();
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to find order status history", e);
        }
    }

    @Override
    public OrderStatusHistoryEntity save(OrderStatusHistoryEntity history) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var future = scope.fork(() -> jpaOrderStatusHistoryRepository.save(history));
            scope.join();
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save order status history", e);
        }
    }

    @Override
    public Optional<ShippingEntity> findByOrderId(UUID orderId) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var future = scope.fork(() -> jpaShippingRepository.findByOrderId(orderId));
            scope.join();
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to find shipping by order id", e);
        }
    }

    @Override
    public ShippingEntity save(ShippingEntity shipping) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var future = scope.fork(() -> jpaShippingRepository.save(shipping));
            scope.join();
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save shipping", e);
        }
    }
}
