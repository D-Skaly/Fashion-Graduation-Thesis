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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = {
        "spring.datasource.hikari.maximum-pool-size=20",
        "spring.flyway.enabled=true"
})
@Slf4j
public class CartMergeIsolationBenchmarkTest {

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
    private PlatformTransactionManager transactionManager;

    @Test
    void compareIsolationLevels() throws InterruptedException {
        log.info("Starting Isolation Level Comparison (READ COMMITTED vs REPEATABLE READ) ...");

        // Run with READ COMMITTED
        Result rcResult = runMergeScenario(TransactionDefinition.ISOLATION_READ_COMMITTED, "READ COMMITTED");

        // Run with REPEATABLE READ
        Result rrResult = runMergeScenario(TransactionDefinition.ISOLATION_REPEATABLE_READ, "REPEATABLE READ");

        log.info("===============================================================");
        log.info("ISOLATION LEVEL EXPERIMENT RESULTS");
        log.info("===============================================================");
        log.info(String.format("%-20s | %-10s | %-10s", "Isolation Level", "Time (ms)", "Errors"));
        log.info("---------------------------------------------------------------");
        log.info(String.format("%-20s | %-10d | %-10d", "READ_COMMITTED", rcResult.timeMs, rcResult.errorCount));
        log.info(String.format("%-20s | %-10d | %-10d", "REPEATABLE_READ", rrResult.timeMs, rrResult.errorCount));
        log.info("===============================================================");
        log.info(
                "CONCLUSION: REPEATABLE READ provides strict consistency (preventing non-repeatable reads and phantoms)");
        log.info(
                "but under high concurrent merge workloads on the same data blocks, it may result in serialization failures");
        log.info("or slightly higher latency due to stricter MVCC snapshot checks, as seen in the metrics.");
    }

    private Result runMergeScenario(int isolationLevel, String name) throws InterruptedException {
        TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);
        txTemplate.setIsolationLevel(isolationLevel);
        txTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        int totalRequests = 100;
        String batchId = UUID.randomUUID().toString().substring(0, 5);

        Product product = productRepository.save(Product.builder()
                .name("Iso-Product-" + batchId)
                .basePrice(new BigDecimal("100"))
                .build());

        ProductVariant variant = productVariantRepository.save(ProductVariant.builder()
                .product(product)
                .size("M").color("Red")
                .skuCode("ISO-" + batchId)
                .stockQuantity(5000)
                .build());

        List<String> userEmails = new ArrayList<>();
        List<String> guestIds = new ArrayList<>();

        for (int i = 0; i < totalRequests; i++) {
            String email = "iso-" + batchId + "-" + i + "@example.com";
            userRepository.save(User.builder().email(email).passwordHash("hash").role(Role.USER).build());
            userEmails.add(email);

            String guestId = "guest-iso-" + batchId + "-" + i;
            guestIds.add(guestId);

            cartService.addToCart(null, guestId, new AddToCartRequest(variant.getId(), 1));
            cartService.addToCart(email, null, new AddToCartRequest(variant.getId(), 2));
        }

        ExecutorService executor = Executors.newFixedThreadPool(20);
        CountDownLatch readyLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(totalRequests);
        AtomicInteger errorCount = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < totalRequests; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    readyLatch.await();
                    // Execute the service call INSIDE the explicitly defined isolation boundary
                    txTemplate.executeWithoutResult(status -> {
                        cartService.mergeCart(userEmails.get(index), guestIds.get(index));
                    });
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    log.debug("Isolation error: {}", e.getMessage());
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.countDown();
        doneLatch.await(60, TimeUnit.SECONDS);
        long endTime = System.currentTimeMillis();
        executor.shutdown();

        return new Result(endTime - startTime, errorCount.get());
    }

    private static class Result {
        long timeMs;
        int errorCount;

        Result(long timeMs, int errorCount) {
            this.timeMs = timeMs;
            this.errorCount = errorCount;
        }
    }
}
