package com.skaly.fashion_backend.cart;

import com.skaly.fashion_backend.cart.event.CartMergedEvent;
import com.skaly.fashion_backend.product.infrastructure.persistence.jpa.ProductEntity;
import com.skaly.fashion_backend.product.domain.port.ProductRepository;
import com.skaly.fashion_backend.product.infrastructure.persistence.jpa.ProductVariantEntity;
import com.skaly.fashion_backend.product.domain.port.ProductVariantRepository;
import com.skaly.fashion_backend.user.Role;
import com.skaly.fashion_backend.user.User;
import com.skaly.fashion_backend.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RecordApplicationEvents
public class CartModuleIntegrationTest {

    @MockitoBean
    private VectorStore vectorStore;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private ApplicationEvents applicationEvents;

    @Test
    void shouldPublishCartMergedEventWhenCartIsMerged() {
        // Arrange
        String email = "merge-test-" + UUID.randomUUID() + "@example.com";
        User user = userRepository.save(User.builder()
                .email(email)
                .passwordHash("pass")
                .role(Role.USER)
                .build());

        String guestId = "guest-" + UUID.randomUUID();

        ProductEntity product = productRepository.save(ProductEntity.builder()
                .name("Test ProductEntity")
                .basePrice(BigDecimal.TEN)
                .sku("SKU-" + UUID.randomUUID())
                .slug("slug-" + UUID.randomUUID())
                .build());

        ProductVariantEntity variant = productVariantRepository.save(ProductVariantEntity.builder()
                .product(product)
                .size("L")
                .color("Red")
                .skuCode("SKU-VAR-" + UUID.randomUUID())
                .stockQuantity(10)
                .priceAdjustment(BigDecimal.ZERO)
                .build());

        // Add item to guest cart
        cartService.addToCart(null, guestId, new AddToCartRequest(variant.getId(), 2));

        // Act
        cartService.mergeCart(email, guestId);

        // Assert
        long count = applicationEvents.stream(CartMergedEvent.class)
                .filter(event -> event.userId().equals(user.getId()) && event.guestId().equals(guestId))
                .count();

        assertThat(count).isEqualTo(1);
    }
}
