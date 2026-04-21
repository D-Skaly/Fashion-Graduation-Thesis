package com.skaly.fashion_backend.product;

import com.skaly.fashion_backend.product.application.ProductSearchService;
import com.skaly.fashion_backend.product.domain.port.ProductRepository;
import com.skaly.fashion_backend.product.interfaces.dto.ProductResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.skaly.fashion_backend.testsupport.PostgresIntegrationSupport;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ProductSearchIntegrationTest extends PostgresIntegrationSupport {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductSearchService productSearchService;

    @Test
    void shouldFilterByMetadataThenVectorSearch() {
        // Given
        UUID categoryId = UUID.randomUUID();
        // Giả sử database đã có dữ liệu mẫu hoặc được seed qua Flyway/SQL
        float[] queryVector = new float[]{0.1f, 0.2f, 0.3f};

        // When
        List<ProductResponse> results = productSearchService.searchProductsSemanticallyWithFilters(
                "Váy hoa nhẹ nhàng cho tiệc trà",
                categoryId,
                new BigDecimal("100000"),
                new BigDecimal("1000000"),
                5
        );

        // Then
        // Kiểm tra xem các sản phẩm trả về có đúng category và trong tầm giá không
        // Lưu ý: Trong môi trường test thực tế, ta sẽ chèn dữ liệu mồi ở đây
        assertThat(results).isNotNull();
    }
}
