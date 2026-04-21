package com.skaly.fashion_backend.product;

import com.skaly.fashion_backend.product.application.ProductService;
import com.skaly.fashion_backend.product.domain.model.Category;
import com.skaly.fashion_backend.product.domain.model.Product;
import com.skaly.fashion_backend.product.domain.port.CategoryRepository;
import com.skaly.fashion_backend.product.domain.port.ProductRepository;
import com.skaly.fashion_backend.product.interfaces.dto.CreateProductRequest;
import com.skaly.fashion_backend.product.interfaces.dto.ProductResponse;
import com.skaly.fashion_backend.testsupport.PostgresIntegrationSupport;
import org.junit.jupiter.api.Test;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ProductSemanticSearchTest extends PostgresIntegrationSupport {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @MockitoBean
    private EmbeddingModel embeddingModel;

    @Test
    void testSemanticSearchFlow() throws Exception {
        float[] fakeEmbedding1 = new float[384];
        fakeEmbedding1[0] = 0.9f;

        float[] fakeEmbedding2 = new float[384];
        fakeEmbedding2[0] = 0.1f;

        when(embeddingModel.embed(anyString())).thenAnswer(invocation -> {
            String q = invocation.getArgument(0).toString().toLowerCase();
            if (q.contains("dress") || q.contains("đầm")) {
                return fakeEmbedding1;
            }
            return fakeEmbedding2;
        });

        Category savedCat = categoryRepository.save(Category.builder()
                .name("Women " + UUID.randomUUID().toString().substring(0, 8))
                .description("Women Clothing")
                .slug("women-" + UUID.randomUUID().toString().substring(0, 8))
                .build());

        CreateProductRequest req = new CreateProductRequest(
                "Đầm dạ hội màu đen",
                "100% lụa",
                BigDecimal.valueOf(100),
                savedCat.getId(),
                List.of());
        ProductResponse res = productService.createProduct(req);

        await().atMost(5, TimeUnit.SECONDS)
                .until(() -> productRepository.findById(res.id())
                        .map(p -> p.getEmbeddingVector() != null)
                        .orElse(false));

        Product savedProduct = productRepository.findById(res.id()).orElseThrow();
        assertThat(savedProduct.getEmbeddingVector()).isNotNull();
        assertThat(savedProduct.getEmbeddingVector()[0]).isEqualTo(0.9f);

        List<ProductResponse> searchRes = productService.searchProductsSemantically("dress", 5);

        assertThat(searchRes).isNotEmpty();
        assertThat(searchRes.get(0).name()).isEqualTo("Đầm dạ hội màu đen");
    }
}
