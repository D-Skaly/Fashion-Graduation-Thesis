package com.skaly.fashion_backend.cart.infrastructure.persistence.jpa;

import com.skaly.fashion_backend.cart.domain.entities.Cart;
import com.skaly.fashion_backend.cart.domain.entities.CartItem;
import com.skaly.fashion_backend.cart.application.CartItemRepository;
import com.skaly.fashion_backend.cart.application.CartRepository;
import com.skaly.fashion_backend.product.domain.port.ProductVariantRepository;
import com.skaly.fashion_backend.user.infrastructure.persistence.entities.UserEntity;
import com.skaly.fashion_backend.product.infrastructure.persistence.jpa.ProductVariantEntity;
import com.skaly.fashion_backend.cart.infrastructure.persistence.entities.CartEntity;
import com.skaly.fashion_backend.cart.infrastructure.persistence.entities.CartItemEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.StructuredTaskScope;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CartPersistenceAdapter implements CartRepository, CartItemRepository {

    private final JpaCartRepository jpaCartRepository;
    private final JpaCartItemRepository jpaCartItemRepository;
    
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Cart> findByUserId(UUID userId) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var future = scope.fork(() -> jpaCartRepository.findByUserId(userId).map(this::toDomain));
            scope.join();
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to find cart by user id", e);
        }
    }

    @Override
    public Optional<Cart> findByGuestId(String guestId) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var future = scope.fork(() -> jpaCartRepository.findByGuestId(guestId).map(this::toDomain));
            scope.join();
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to find cart by guest id", e);
        }
    }

    @Override
    public Optional<Cart> findById(UUID id) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var future = scope.fork(() -> jpaCartRepository.findById(id).map(this::toDomain));
            scope.join();
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to find cart by id", e);
        }
    }

    @Override
    public Cart save(Cart cart) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var future = scope.fork(() -> {
                CartEntity entity = toEntity(cart);
                return toDomain(jpaCartRepository.save(entity));
            });
            scope.join();
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save cart", e);
        }
    }

    @Override
    public void delete(Cart cart) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var future = scope.fork(() -> {
                jpaCartRepository.delete(toEntity(cart));
                return null;
            });
            scope.join();
            future.get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete cart", e);
        }
    }

    @Override
    public List<Cart> findAbandonedGuestCarts(LocalDateTime thresholdDate) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var future = scope.fork(() -> jpaCartRepository.findByGuestIdIsNotNullAndUpdatedAtBefore(thresholdDate)
                    .stream()
                    .map(this::toDomain)
                    .collect(Collectors.toList()));
            scope.join();
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to find abandoned guest carts", e);
        }
    }

    @Override
    public Optional<CartItem> findItemById(UUID id) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var future = scope.fork(() -> jpaCartItemRepository.findById(id).map(this::toDomainItem));
            scope.join();
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to find cart item by id", e);
        }
    }

    private Cart toDomain(CartEntity entity) {
        return Cart.builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .guestId(entity.getGuestId())
                .couponCode(entity.getCouponCode())
                .discountAmount(entity.getDiscountAmount())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .items(entity.getItems().stream().map(this::toDomainItem).collect(Collectors.toList()))
                .build();
    }

    private CartItem toDomainItem(CartItemEntity entity) {
        return CartItem.builder()
                .id(entity.getId())
                .productVariantId(entity.getProductVariant().getId())
                .quantity(entity.getQuantity())
                .snapshotPrice(entity.getSnapshotPrice())
                .addedAt(entity.getAddedAt())
                .quantityAdjusted(entity.isQuantityAdjusted())
                .build();
    }

    private CartEntity toEntity(Cart cart) {
        CartEntity entity = CartEntity.builder()
                .id(cart.getId())
                .user(cart.getUserId() != null ? entityManager.getReference(UserEntity.class, cart.getUserId()) : null)
                .guestId(cart.getGuestId())
                .couponCode(cart.getCouponCode())
                .discountAmount(cart.getDiscountAmount())
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .build();
        
        if (cart.getItems() != null) {
            List<CartItemEntity> itemEntities = cart.getItems().stream()
                    .map(item -> toEntityItem(item, entity))
                    .collect(Collectors.toList());
            entity.setItems(itemEntities);
        }
        
        return entity;
    }

    private CartItemEntity toEntityItem(CartItem item, CartEntity cartEntity) {
        return CartItemEntity.builder()
                .id(item.getId())
                .cart(cartEntity)
                .productVariant(entityManager.getReference(ProductVariantEntity.class, item.getProductVariantId()))
                .quantity(item.getQuantity())
                .snapshotPrice(item.getSnapshotPrice())
                .addedAt(item.getAddedAt())
                .quantityAdjusted(item.isQuantityAdjusted())
                .build();
    }
}

