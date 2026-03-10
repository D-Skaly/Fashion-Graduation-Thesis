package com.skaly.fashion_backend.cart;

import com.skaly.fashion_backend.product.Product;
import com.skaly.fashion_backend.product.ProductRepository;
import com.skaly.fashion_backend.product.ProductVariant;
import com.skaly.fashion_backend.product.ProductVariantRepository;
import com.skaly.fashion_backend.user.Role;
import com.skaly.fashion_backend.user.User;
import com.skaly.fashion_backend.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = {
        // Override datasource pool size to comfortably handle 1000 concurrent threads
        // without excessive queueing timeouts
        "spring.datasource.hikari.maximum-pool-size=50",
        // Disable Spring AI just like in application-test.properties to let context
        // load
})
@Slf4j
public class CartMergeBenchmarkTest {

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
    private CartRepository cartRepository;

    @Test
    void benchmarkCartMergeUnder1000ConcurrentLogins() throws InterruptedException {
        int totalRequests = 1000;

        // 1. Setup global product
        Product product = productRepository.save(Product.builder()
                .name("Benchmark Shirt")
                .description("Perf test")
                .basePrice(new BigDecimal("100"))
                .build());

        ProductVariant variant = productVariantRepository.save(ProductVariant.builder()
                .product(product)
                .size("M")
                .color("Black")
                .skuCode("B-M-BLK-" + UUID.randomUUID())
                .stockQuantity(10000) // Ensure enough stock
                .priceAdjustment(BigDecimal.ZERO)
                .build());

        // 2. Prepare 1000 Users and Guest Carts
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

            // Add item to guest cart
            cartService.addToCart(null, guestId, new AddToCartRequest(variant.getId(), 1));
            // Ensure user cart is initialized too
            cartService.addToCart(email, null, new AddToCartRequest(variant.getId(), 2));
        }

        // 3. Setup Concurrency
        log.info("Starting merge benchmark...");
        ExecutorService executorService = Executors.newFixedThreadPool(100); // 100 concurrent system threads
        CountDownLatch readyLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(totalRequests);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < totalRequests; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    readyLatch.await(); // wait until all threads are queued
                    cartService.mergeCart(userEmails.get(index), guestIds.get(index));
                } catch (Exception e) {
                    log.error("Merge failed", e);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        // 4. BLAST OFF
        long executionStartTime = System.currentTimeMillis();
        readyLatch.countDown();

        // Wait for all to finish, wait up to 2 minutes
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

        // Sanity check an arbitrary user
        CartDto userCart = cartService.getCart(userEmails.get(0), null);
        assertThat(userCart.items().get(0).quantity()).isEqualTo(3); // 2 initially + 1 merged
    }
}
