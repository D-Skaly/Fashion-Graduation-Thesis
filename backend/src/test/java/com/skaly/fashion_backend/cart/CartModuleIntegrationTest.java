package com.skaly.fashion_backend.cart;

import com.skaly.fashion_backend.cart.api.dto.AddToCartRequest;
import com.skaly.fashion_backend.cart.application.CartService;
import com.skaly.fashion_backend.cart.event.CartMergedEvent;
import com.skaly.fashion_backend.product.domain.model.Product;
import com.skaly.fashion_backend.product.domain.port.ProductRepository;
import com.skaly.fashion_backend.testsupport.ProductCatalogTestData;
import com.skaly.fashion_backend.user.Role;
import com.skaly.fashion_backend.user.UserRepository;
import com.skaly.fashion_backend.testsupport.PostgresIntegrationSupport;
import com.skaly.fashion_backend.user.domain.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RecordApplicationEvents
@Transactional
public class CartModuleIntegrationTest extends PostgresIntegrationSupport {

    @MockitoBean
    private VectorStore vectorStore;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ApplicationEvents applicationEvents;

    @Test
    void shouldPublishCartMergedEventWhenCartIsMerged() {
        String email = "merge-test-" + UUID.randomUUID() + "@example.com";
        User user = userRepository.save(User.builder()
                .email(email)
                .passwordHash("pass")
                .role(Role.USER)
                .build());

        String guestId = "guest-" + UUID.randomUUID();

        Product saved = ProductCatalogTestData.saveProductWithSingleVariant(
                productRepository, "Test ProductEntity", BigDecimal.TEN, "L", "Red", 10);
        UUID variantId = ProductCatalogTestData.firstVariantIdMatchingStock(saved, 10);

        cartService.addToCart(null, guestId, new AddToCartRequest(variantId, 2));

        cartService.mergeCart(email, guestId);

        long count = applicationEvents.stream(CartMergedEvent.class)
                .filter(event -> event.userId().equals(user.getId()) && event.guestId().equals(guestId))
                .count();

        assertThat(count).isEqualTo(1);
    }
}
