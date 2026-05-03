package com.skaly.fashion_backend.cart;

import com.skaly.fashion_backend.cart.api.dto.AddToCartRequest;
import com.skaly.fashion_backend.cart.api.dto.CartDto;
import com.skaly.fashion_backend.cart.application.CartService;
import com.skaly.fashion_backend.coupon.infrastructure.persistence.jpa.CouponEntity;
import com.skaly.fashion_backend.coupon.infrastructure.persistence.jpa.CouponJpaRepository;
import com.skaly.fashion_backend.product.domain.model.Product;
import com.skaly.fashion_backend.product.domain.model.ProductVariant;
import com.skaly.fashion_backend.product.domain.port.ProductRepository;
import com.skaly.fashion_backend.product.domain.port.ProductVariantRepository;
import com.skaly.fashion_backend.testsupport.ProductCatalogTestData;
import com.skaly.fashion_backend.user.Role;
import com.skaly.fashion_backend.user.UserRepository;
import com.skaly.fashion_backend.testsupport.PostgresIntegrationSupport;
import com.skaly.fashion_backend.user.domain.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CartServiceTest extends PostgresIntegrationSupport {

    @MockBean
    private VectorStore vectorStore;

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CouponJpaRepository couponJpaRepository;

    private User testUser;
    private UUID testVariantId;
    private UUID outOfStockVariantId;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@example.com")
                .passwordHash("hash")
                .role(Role.USER)
                .build();
        userRepository.save(testUser);

        Product saved = ProductCatalogTestData.saveProductWithTwoVariants(
                productRepository, "Test Shirt", new BigDecimal("100.00"), 10, 0);
        testVariantId = ProductCatalogTestData.firstVariantIdMatchingStock(saved, 10);
        outOfStockVariantId = ProductCatalogTestData.firstVariantIdMatchingStock(saved, 0);
    }

    @Test
    void shouldCreateGuestCartAndAddItems() {
        AddToCartRequest request = new AddToCartRequest(testVariantId, 2);
        String guestId = "guest-123";

        CartDto cartDto = cartService.addToCart(null, guestId, request);

        assertThat(cartDto.guestId()).isEqualTo(guestId);
        assertThat(cartDto.items()).hasSize(1);
        assertThat(cartDto.items().get(0).quantity()).isEqualTo(2);
        assertThat(cartDto.subTotal()).isEqualTo(new BigDecimal("200.00"));
    }

    @Test
    void shouldMergeGuestCartIntoUserCartAndApplyNewestPrice() {
        String guestId = "merge-guest";
        cartService.addToCart(null, guestId, new AddToCartRequest(testVariantId, 1));

        cartService.addToCart(testUser.getEmail(), null, new AddToCartRequest(testVariantId, 2));

        CartDto mergedCart = cartService.mergeCart(testUser.getEmail(), guestId);

        assertThat(mergedCart.guestId()).isNull();
        assertThat(mergedCart.items()).hasSize(1);
        assertThat(mergedCart.items().get(0).quantity()).isEqualTo(3);

        assertThat(cartRepository.findByGuestId(guestId)).isEmpty();
    }

    @Test
    void shouldAutoRemoveOutOfStockAndAdjustExceedingQuantity() {
        String guestId = "inventory-guest";

        cartService.addToCart(null, guestId, new AddToCartRequest(testVariantId, 5));

        cartService.addToCart(null, guestId, new AddToCartRequest(outOfStockVariantId, 1));

        CartDto cart = cartService.getCart(null, guestId);

        assertThat(cart.items()).hasSize(1);
        assertThat(cart.items().get(0).productVariantId()).isEqualTo(testVariantId);
        assertThat(cart.items().get(0).quantity()).isEqualTo(5);

        ProductVariant v = productVariantRepository.findVariantById(testVariantId).orElseThrow();
        v.setStockQuantity(2);
        productVariantRepository.save(v);

        CartDto adjustedCart = cartService.getCart(null, guestId);

        assertThat(adjustedCart.items().get(0).quantity()).isEqualTo(2);
        assertThat(adjustedCart.items().get(0).quantityAdjusted()).isTrue();
    }

    @Test
    void shouldApplyCouponSuccessfully() {
        String guestId = "coupon-guest";
        cartService.addToCart(null, guestId, new AddToCartRequest(testVariantId, 2));

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
