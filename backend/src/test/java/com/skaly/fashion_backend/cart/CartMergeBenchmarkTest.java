package com.skaly.fashion_backend.cart;

import com.skaly.fashion_backend.cart.api.dto.AddToCartRequest;
import com.skaly.fashion_backend.cart.api.dto.CartDto;
import com.skaly.fashion_backend.cart.application.CartService;
import com.skaly.fashion_backend.product.domain.model.Product;
import com.skaly.fashion_backend.product.domain.port.ProductRepository;
import com.skaly.fashion_backend.testsupport.ProductCatalogTestData;
import com.skaly.fashion_backend.user.Role;
import com.skaly.fashion_backend.user.UserRepository;
import com.skaly.fashion_backend.testsupport.PostgresIntegrationSupport;
import com.skaly.fashion_backend.user.domain.entities.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.hikari.maximum-pool-size=50",
})
@Slf4j
public class CartMergeBenchmarkTest extends PostgresIntegrationSupport {

    @MockitoBean
    private VectorStore vectorStore;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Test
    void benchmarkCartMergeUnder1000ConcurrentLogins() throws InterruptedException {
        int totalRequests = 1000;

        Product saved = ProductCatalogTestData.saveProductWithSingleVariant(
                productRepository,
                "Benchmark Shirt",
                new BigDecimal("100"),
                "L",
                "Red",
                10000);
        UUID variantId = ProductCatalogTestData.firstVariantIdMatchingStock(saved, 10000);

        log.info("Setting up {} users and guest carts for benchmark...", totalRequests);
        List<String> userEmails = new ArrayList<>();
        List<String> guestIds = new ArrayList<>();

        String batchId = UUID.randomUUID().toString().substring(0, 5);
        for (int i = 0; i < totalRequests; i++) {
            String email = "perfuser-" + batchId + "-" + i + "@example.com";
            User user = User.builder()
                    .email(email)
                    .passwordHash("hash")
                    .role(Role.USER)
                    .build();
            userRepository.save(user);
            userEmails.add(email);

            String guestId = "guest-perf-" + batchId + "-" + i;
            guestIds.add(guestId);

            cartService.addToCart(null, guestId, new AddToCartRequest(variantId, 1));
            cartService.addToCart(email, null, new AddToCartRequest(variantId, 2));
        }

        log.info("Starting merge benchmark...");
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        CountDownLatch readyLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(totalRequests);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < totalRequests; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    readyLatch.await();
                    cartService.mergeCart(userEmails.get(index), guestIds.get(index));
                } catch (Exception e) {
                    log.error("Merge failed", e);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        long executionStartTime = System.currentTimeMillis();
        readyLatch.countDown();

        boolean completed = doneLatch.await(120, TimeUnit.SECONDS);
        long endTime = System.currentTimeMillis();
        executorService.shutdown();

        assertThat(completed).isTrue();

        long totalExecutionTime = endTime - executionStartTime;
        long totalSetupTime = executionStartTime - startTime;

        log.info("==========================================");
        log.info("Cart Merge Benchmark Results (1000 Merges)");
        log.info("==========================================");
        log.info("Setup Time        : {} ms", totalSetupTime);
        log.info("Execution Time    : {} ms", totalExecutionTime);
        log.info("Throughput        : {} requests/second", (totalRequests * 1000.0) / totalExecutionTime);
        log.info("==========================================");

        CartDto userCart = cartService.getCart(userEmails.get(0), null);
        assertThat(userCart.items().get(0).quantity()).isEqualTo(3);
    }
}
