package com.skaly.fashion_backend.cart.application;

import com.skaly.fashion_backend.cart.api.dto.*;
import com.skaly.fashion_backend.cart.domain.entities.Cart;
import com.skaly.fashion_backend.cart.domain.entities.CartItem;
import com.skaly.fashion_backend.cart.application.CartItemRepository;
import com.skaly.fashion_backend.cart.application.CartRepository;
import com.skaly.fashion_backend.cart.event.CartMergedEvent;
import com.skaly.fashion_backend.common.domain.ResourceNotFoundException;
import com.skaly.fashion_backend.coupon.application.CouponService;
import com.skaly.fashion_backend.product.domain.port.ProductCartServicePort;
import com.skaly.fashion_backend.product.domain.port.dto.ProductVariantInfo;
import com.skaly.fashion_backend.user.domain.entities.User;
import com.skaly.fashion_backend.user.infrastructure.persistence.jpa.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductCartServicePort productCartServicePort;
    private final UserRepository userRepository;
    private final CouponService couponService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public CartDto getCart(String userEmail, String guestId) {
        Cart cart = resolveCart(userEmail, guestId);
        validateInventoryAndPrices(cart);
        validateAndApplyCoupon(cart);
        return mapToDto(cart);
    }

    @Transactional
    public CartDto addToCart(String userEmail, String guestId, AddToCartRequest request) {
        Cart cart = resolveCart(userEmail, guestId);
        ProductVariantInfo variant = productCartServicePort.getProductVariantInfo(request.productVariantId());

        if (variant == null) {
            throw new ResourceNotFoundException("Product variant not found: " + request.productVariantId());
        }

        BigDecimal currentPrice = variant.basePrice();
        if (variant.priceAdjustment() != null) {
            currentPrice = currentPrice.add(variant.priceAdjustment());
        }

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductVariantId().equals(variant.id()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.quantity());
            item.setSnapshotPrice(currentPrice);
        } else {
            CartItem newItem = CartItem.builder()
                    .productVariantId(variant.id())
                    .quantity(request.quantity())
                    .snapshotPrice(currentPrice)
                    .addedAt(LocalDateTime.now())
                    .build();
            cart.addItem(newItem);
        }

        validateInventoryAndPrices(cart);
        validateAndApplyCoupon(cart);
        Cart savedCart = cartRepository.save(cart);
        return mapToDto(savedCart);
    }

    @Transactional
    public CartDto updateCartItem(String userEmail, String guestId, UpdateCartRequest request) {
        Cart cart = resolveCart(userEmail, guestId);

        Optional<CartItem> itemInCart = cart.getItems().stream()
                .filter(i -> i.getId().equals(request.cartItemId()))
                .findFirst();
        
        if (itemInCart.isEmpty()) {
            throw new ResourceNotFoundException("Cart Item not found in cart");
        }

        CartItem cartItem = itemInCart.get();
        if (request.quantity() <= 0) {
            cart.removeItem(cartItem.getId());
        } else {
            cartItem.setQuantity(request.quantity());
        }

        validateInventoryAndPrices(cart);
        validateAndApplyCoupon(cart);
        Cart savedCart = cartRepository.save(cart);
        return mapToDto(savedCart);
    }

    @Transactional
    public CartDto removeCartItem(String userEmail, String guestId, UUID cartItemId) {
        Cart cart = resolveCart(userEmail, guestId);
        cart.removeItem(cartItemId);
        
        validateInventoryAndPrices(cart);
        validateAndApplyCoupon(cart);
        Cart savedCart = cartRepository.save(cart);
        return mapToDto(savedCart);
    }

    @Transactional
    public CartDto applyCoupon(String userEmail, String guestId, String couponCode) {
        Cart cart = resolveCart(userEmail, guestId);
        cart.setCouponCode(couponCode);
        validateAndApplyCoupon(cart);
        Cart savedCart = cartRepository.save(cart);
        return mapToDto(savedCart);
    }

    @Transactional
    public void clearCart(String userEmail, String guestId) {
        Cart cart = resolveCart(userEmail, guestId);
        cart.clearItems();
        cart.setCouponCode(null);
        cart.setDiscountAmount(BigDecimal.ZERO);
        cartRepository.save(cart);
    }

    @Transactional
    public CartDto mergeCart(String userEmail, String guestId) {
        if (userEmail == null || guestId == null) {
            return getCart(userEmail, guestId);
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Optional<Cart> guestCartOpt = cartRepository.findByGuestId(guestId);
        Optional<Cart> userCartOpt = cartRepository.findByUserId(user.getId());

        if (guestCartOpt.isEmpty()) {
            return userCartOpt.map(this::mapToDto).orElseGet(() -> getCart(userEmail, guestId));
        }

        Cart guestCart = guestCartOpt.get();
        if (userCartOpt.isEmpty()) {
            String guestIdForEvent = guestCart.getGuestId();
            int guestLineItems = guestCart.getItems().size();
            guestCart.setUserId(user.getId());
            guestCart.setGuestId(null);
            Cart saved = cartRepository.save(guestCart);
            eventPublisher.publishEvent(new CartMergedEvent(
                    user.getId(),
                    guestIdForEvent != null ? guestIdForEvent : "",
                    guestLineItems));
            return mapToDto(saved);
        }

        Cart userCart = userCartOpt.get();
        List<UUID> mergedItemIds = new ArrayList<>();

        for (CartItem guestItem : guestCart.getItems()) {
            Optional<CartItem> existingItem = userCart.getItems().stream()
                    .filter(item -> item.getProductVariantId().equals(guestItem.getProductVariantId()))
                    .findFirst();

            if (existingItem.isPresent()) {
                existingItem.get().setQuantity(existingItem.get().getQuantity() + guestItem.getQuantity());
                mergedItemIds.add(existingItem.get().getId());
            } else {
                CartItem newItem = CartItem.builder()
                        .productVariantId(guestItem.getProductVariantId())
                        .quantity(guestItem.getQuantity())
                        .snapshotPrice(guestItem.getSnapshotPrice())
                        .addedAt(guestItem.getAddedAt())
                        .build();
                userCart.addItem(newItem);
            }
        }

        String guestIdForEvent = guestCart.getGuestId();
        cartRepository.delete(guestCart);
        Cart savedUserCart = cartRepository.save(userCart);

        eventPublisher.publishEvent(new CartMergedEvent(
                user.getId(),
                guestIdForEvent != null ? guestIdForEvent : "",
                mergedItemIds.size()));

        return mapToDto(savedUserCart);
    }

    private Cart resolveCart(String userEmail, String guestId) {
        if (userEmail != null) {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            return cartRepository.findByUserId(user.getId())
                    .orElseGet(() -> {
                        Cart newCart = Cart.builder()
                                .userId(user.getId())
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();
                        return cartRepository.save(newCart);
                    });
        } else if (guestId != null) {
            return cartRepository.findByGuestId(guestId)
                    .orElseGet(() -> {
                        Cart newCart = Cart.builder()
                                .guestId(guestId)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();
                        return cartRepository.save(newCart);
                    });
        }
        throw new IllegalArgumentException("Either userEmail or guestId must be provided");
    }

    private void validateInventoryAndPrices(Cart cart) {
        List<UUID> variantIds = cart.getItems().stream()
                .map(com.skaly.fashion_backend.cart.domain.entities.CartItem::getProductVariantId)
                .toList();
        Map<UUID, ProductVariantInfo> variants = productCartServicePort.getProductVariantsBatch(variantIds);

        for (CartItem item : cart.getItems()) {
            ProductVariantInfo variant = variants.get(item.getProductVariantId());
            if (variant == null) {
                continue; // Skip items with invalid variants
            }

            BigDecimal currentPrice = variant.basePrice();
            if (variant.priceAdjustment() != null) {
                currentPrice = currentPrice.add(variant.priceAdjustment());
            }

            if (!item.getSnapshotPrice().equals(currentPrice)) {
                item.setSnapshotPrice(currentPrice);
            }

            if (variant.stockQuantity() != null && variant.stockQuantity() < item.getQuantity()) {
                item.setQuantity(variant.stockQuantity());
                item.setQuantityAdjusted(true);
            }
        }
        cart.getItems().removeIf(item -> item.getQuantity() <= 0);
    }

    private void validateAndApplyCoupon(Cart cart) {
        if (cart.getCouponCode() != null) {
            try {
                BigDecimal subtotal = calculateSubTotal(cart);
                BigDecimal discount = couponService.calculateDiscount(cart.getCouponCode(), subtotal);
                cart.setDiscountAmount(discount);
            } catch (Exception e) {
                cart.setCouponCode(null);
                cart.setDiscountAmount(BigDecimal.ZERO);
            }
        } else {
            cart.setDiscountAmount(BigDecimal.ZERO);
        }
    }

    private BigDecimal calculateSubTotal(Cart cart) {
        return cart.getItems().stream()
                .map(item -> item.getSnapshotPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private CartDto mapToDto(Cart cart) {
        List<UUID> variantIds = cart.getItems().stream()
                .map(com.skaly.fashion_backend.cart.domain.entities.CartItem::getProductVariantId)
                .toList();
        Map<UUID, ProductVariantInfo> variants = productCartServicePort.getProductVariantsBatch(variantIds);

        List<CartItemDto> itemDtos = cart.getItems().stream()
                .map(item -> {
                    ProductVariantInfo variant = variants.get(item.getProductVariantId());
                    if (variant == null) {
                        // Return DTO with default values for missing variant
                        return new CartItemDto(
                                item.getId(),
                                item.getProductVariantId(),
                                "Unknown Product",
                                "-",
                                "-",
                                item.getSnapshotPrice(),
                                item.getSnapshotPrice(),
                                item.getQuantity(),
                                item.getSnapshotPrice().multiply(BigDecimal.valueOf(item.getQuantity())),
                                true,
                                item.isQuantityAdjusted()
                        );
                    }

                    BigDecimal currentUnit = variant.basePrice();
                    if (variant.priceAdjustment() != null) {
                        currentUnit = currentUnit.add(variant.priceAdjustment());
                    }
                    BigDecimal lineSubtotal = item.getSnapshotPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()));
                    boolean outOfStock = variant.stockQuantity() == null
                            || variant.stockQuantity() <= 0
                            || variant.stockQuantity() < item.getQuantity();
                    return new CartItemDto(
                            item.getId(),
                            item.getProductVariantId(),
                            variant.productName(),
                            variant.size(),
                            variant.color(),
                            currentUnit,
                            item.getSnapshotPrice(),
                            item.getQuantity(),
                            lineSubtotal,
                            outOfStock,
                            item.isQuantityAdjusted()
                    );
                })
                .collect(Collectors.toList());

        BigDecimal subTotal = calculateSubTotal(cart);
        BigDecimal total = subTotal.subtract(cart.getDiscountAmount()).max(BigDecimal.ZERO);

        return new CartDto(
                cart.getId(),
                cart.getGuestId(),
                itemDtos,
                cart.getCouponCode(),
                cart.getDiscountAmount(),
                subTotal,
                total
        );
    }

                    BigDecimal currentUnit = variant.basePrice();
                    if (variant.priceAdjustment() != null) {
                        currentUnit = currentUnit.add(variant.priceAdjustment());
                    }
                    BigDecimal lineSubtotal = item.getSnapshotPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()));
                    boolean outOfStock = variant.stockQuantity() == null
                            || variant.stockQuantity() <= 0
                            || variant.stockQuantity() < item.getQuantity();
                    return new CartItemDto(
                            item.getId(),
                            item.getProductVariantId(),
                            variant.productName(),
                            variant.size(),
                            variant.color(),
                            currentUnit,
                            item.getSnapshotPrice(),
                            item.getQuantity(),
                            lineSubtotal,
                            outOfStock,
                            item.isQuantityAdjusted()
                    );
                })
                .collect(Collectors.toList());

        BigDecimal subTotal = calculateSubTotal(cart);
        BigDecimal total = subTotal.subtract(cart.getDiscountAmount()).max(BigDecimal.ZERO);

        return new CartDto(
                cart.getId(),
                cart.getGuestId(),
                itemDtos,
                cart.getCouponCode(),
                cart.getDiscountAmount(),
                subTotal,
                total
        );
    }
}

