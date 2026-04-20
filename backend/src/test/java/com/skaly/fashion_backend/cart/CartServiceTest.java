package com.skaly.fashion_backend.cart;

import com.skaly.fashion_backend.cart.api.dto.AddToCartRequest;
import com.skaly.fashion_backend.cart.api.dto.CartDto;
import com.skaly.fashion_backend.cart.application.CartService;
import com.skaly.fashion_backend.coupon.infrastructure.persistence.jpa.CouponEntity;
import com.skaly.fashion_backend.coupon.infrastructure.persistence.jpa.CouponJpaRepository;
import org.springframework.ai.vectorstore.VectorStore;
import com.skaly.fashion_backend.product.infrastructure.persistence.jpa.ProductEntity;
import com.skaly.fashion_backend.product.domain.port.ProductRepository;
import com.skaly.fashion_backend.product.infrastructure.persistence.jpa.ProductVariantEntity;
import com.skaly.fashion_backend.product.domain.port.ProductVariantRepository;
import com.skaly.fashion_backend.user.Role;
import com.skaly.fashion_backend.user.User;
import com.skaly.fashion_backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CartServiceTest {

    @MockitoBean
    private VectorStore vectorStore;

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private CouponJpaRepository couponJpaRepository;

    private User testUser;
    private ProductVariantEntity testVariant;
    private ProductVariantEntity outOfStockVariant;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@example.com")
                .passwordHash("hash")
                .role(Role.USER)
                .build();
        userRepository.save(testUser);

        ProductEntity product = ProductEntity.builder()
                .name("Test Shirt")
                .sku("TS-SHIRT-001")
                .slug("test-shirt-001")
                .description("A high quality test shirt")
                .basePrice(new BigDecimal("100.00"))
                .build();
        productRepository.save(product);

        testVariant = ProductVariantEntity.builder()
                .product(product)
                .size("M")
                .color("Red")
                .skuCode("TS-M-R")
                .stockQuantity(10)
                .priceAdjustment(BigDecimal.ZERO)
                .build();

        outOfStockVariant = ProductVariantEntity.builder()
                .product(product)
                .size("S")
                .color("Blue")
                .skuCode("TS-S-B")
                .stockQuantity(0)
                .priceAdjustment(BigDecimal.ZERO)
                .build();

        productVariantRepository.save(testVariant);
        productVariantRepository.save(outOfStockVariant);
    }

    @Test
    void shouldCreateGuestCartAndAddItems() {
        AddToCartRequest request = new AddToCartRequest(testVariant.getId(), 2);
        String guestId = "guest-123";

        CartDto cartDto = cartService.addToCart(null, guestId, request);

        assertThat(cartDto.guestId()).isEqualTo(guestId);
        assertThat(cartDto.items()).hasSize(1);
        assertThat(cartDto.items().get(0).quantity()).isEqualTo(2);
        assertThat(cartDto.subTotal()).isEqualTo(new BigDecimal("200.00"));
    }

    @Test
    void shouldMergeGuestCartIntoUserCartAndApplyNewestPrice() {
        // Setup Guest Cart
        String guestId = "merge-guest";
        cartService.addToCart(null, guestId, new AddToCartRequest(testVariant.getId(), 1));

        // Setup User Cart
        cartService.addToCart(testUser.getEmail(), null, new AddToCartRequest(testVariant.getId(), 2));

        // Perform Merge
        CartDto mergedCart = cartService.mergeCart(testUser.getEmail(), guestId);

        // Verify
        assertThat(mergedCart.guestId()).isNull();
        assertThat(mergedCart.items()).hasSize(1);
        assertThat(mergedCart.items().get(0).quantity()).isEqualTo(3);

        // Assert guest cart deleted
        assertThat(cartRepository.findByGuestId(guestId)).isEmpty();
    }

    @Test
    void shouldAutoRemoveOutOfStockAndAdjustExceedingQuantity() {
        String guestId = "inventory-guest";

        // Add 5 items (stock is 10)
        cartService.addToCart(null, guestId, new AddToCartRequest(testVariant.getId(), 5));

        // Add 1 out of stock item
        cartService.addToCart(null, guestId, new AddToCartRequest(outOfStockVariant.getId(), 1));

        // Act: get cart triggers validation
        CartDto cart = cartService.getCart(null, guestId);

        // Out of stock should be removed
        assertThat(cart.items()).hasSize(1);
        assertThat(cart.items().get(0).productVariantId()).isEqualTo(testVariant.getId());
        assertThat(cart.items().get(0).quantity()).isEqualTo(5);

        // Now simulate stock drops to 2
        testVariant.setStockQuantity(2);
        productVariantRepository.save(testVariant);

        // Act again
        CartDto adjustedCart = cartService.getCart(null, guestId);

        // Should cap at 2 and set flag
        assertThat(adjustedCart.items().get(0).quantity()).isEqualTo(2);
        assertThat(adjustedCart.items().get(0).quantityAdjusted()).isTrue();
    }

    @Test
    void shouldApplyCouponSuccessfully() {
        String guestId = "coupon-guest";
        cartService.addToCart(null, guestId, new AddToCartRequest(testVariant.getId(), 2));
        // Subtotal: 200

        CouponEntity coupon = CouponEntity.builder()
                .code("MINUS50")
                .discountType(CouponEntity.DiscountType.FIXED_AMOUNT)
                .discountValue(new BigDecimal("50.00"))
                .minOrderValue(new BigDecimal("100.00"))
                .isActive(true)
                .build();
        couponJpaRepository.save(coupon);

        CartDto cart = cartService.applyCoupon(null, guestId, "MINUS50");

        assertThat(cart.couponCode()).isEqualTo("MINUS50");
        assertThat(cart.discountAmount()).isEqualTo(new BigDecimal("50.00"));
        assertThat(cart.finalTotal()).isEqualTo(new BigDecimal("150.00"));
    }
}
