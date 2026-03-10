package com.skaly.fashion_backend.product;

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
public class ProductSemanticSearchTest {

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
        // Mock the Gemini embedding model to return dummy vectors
        float[] fakeEmbedding1 = new float[768];
        fakeEmbedding1[0] = 0.9f;

        float[] fakeEmbedding2 = new float[768];
        fakeEmbedding2[0] = 0.1f;

        // When embedding is requested for "black dress", return vector 1
        when(embeddingModel.embed(anyString())).thenAnswer(invocation -> {
            String q = invocation.getArgument(0).toString().toLowerCase();
            if (q.contains("dress") || q.contains("đầm"))
                return fakeEmbedding1;
            return fakeEmbedding2;
        });

        Category cat = new Category();
        cat.setName("Women " + UUID.randomUUID().toString().substring(0, 8));
        cat.setDescription("Women Clothing");
        cat.setSlug("women-" + UUID.randomUUID().toString().substring(0, 8));
        Category savedCat = categoryRepository.save(cat);

        // Create product (this will fire ProductCreatedEvent and trigger Embedding)
        CreateProductRequest req = new CreateProductRequest("Đầm dạ hội màu đen", "100% lụa", BigDecimal.valueOf(100),
                savedCat.getId(), List.of());
        ProductResponse res = productService.createProduct(req);

        // Wait up to 5s for the @Async event listener to save the embedding vector
        await().atMost(5, TimeUnit.SECONDS)
                .until(() -> productRepository.findById(res.id())
                        .map(p -> p.getEmbeddingVector() != null)
                        .orElse(false));

        // Verify vector is saved
        Product savedProduct = productRepository.findById(res.id()).orElseThrow();
        assertThat(savedProduct.getEmbeddingVector()).isNotNull();
        assertThat(savedProduct.getEmbeddingVector()[0]).isEqualTo(0.9f);

        // Search
        List<ProductResponse> searchRes = productService.searchProductsSemantically("dress", 5);

        // Validate search results
        assertThat(searchRes).isNotEmpty();
        assertThat(searchRes.get(0).name()).isEqualTo("Đầm dạ hội màu đen");
    }
}
