package com.skaly.fashion_backend.cart.infrastructure.persistence.jpa;

import com.skaly.fashion_backend.cart.Cart;
import com.skaly.fashion_backend.cart.CartItem;
import com.skaly.fashion_backend.cart.CartItemRepository;
import com.skaly.fashion_backend.cart.CartRepository;
import com.skaly.fashion_backend.product.domain.port.ProductVariantRepository;
import com.skaly.fashion_backend.user.UserEntity;
import com.skaly.fashion_backend.product.infrastructure.persistence.jpa.ProductVariantEntity;
import com.skaly.fashion_backend.cart.CartEntity;
import com.skaly.fashion_backend.cart.CartItemEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
        return jpaCartRepository.findByUserId(userId).map(this::toDomain);
    }

    @Override
    public Optional<Cart> findByGuestId(String guestId) {
        return jpaCartRepository.findByGuestId(guestId).map(this::toDomain);
    }

    @Override
    public Optional<Cart> findById(UUID id) {
        return jpaCartRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Cart save(Cart cart) {
        CartEntity entity = toEntity(cart);
        return toDomain(jpaCartRepository.save(entity));
    }

    @Override
    public void delete(Cart cart) {
        jpaCartRepository.delete(toEntity(cart));
    }

    @Override
    public List<Cart> findAbandonedGuestCarts(LocalDateTime thresholdDate) {
        return jpaCartRepository.findByGuestIdIsNotNullAndUpdatedAtBefore(thresholdDate)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CartItem> findItemById(UUID id) {
        return jpaCartItemRepository.findById(id).map(this::toDomainItem);
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

