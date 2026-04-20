package com.skaly.fashion_backend.ai.application;

import com.skaly.fashion_backend.ai.domain.AIModelPort;
import com.skaly.fashion_backend.product.ProductSearchService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendProductInteractorUnitTest {

    @Mock
    private AIModelPort aiModelPort;

    @Mock
    private ProductSearchService productSearchService;

    @InjectMocks
    private RecommendProductInteractor interactor;

    @Test
    void shouldCallSearchServiceWhenIntentIsRecommend() {
        // Given
        String userMessage = "Tìm cho tôi một bộ váy hoa";
        when(aiModelPort.generateResponse(userMessage)).thenReturn("INTENT: RECOMMEND\nTôi sẽ tìm váy hoa cho bạn.");

        // When
        String response = interactor.handle(userMessage);

        // Then
        assertEquals("Đang tìm kiếm sản phẩm phù hợp với bạn...", response);
        verify(productSearchService, times(1)).searchProductsSemantically(eq(userMessage), anyInt());
    }

    @Test
    void shouldNotCallSearchServiceWhenIntentIsNotRecommend() {
        // Given
        String userMessage = "Chào bạn";
        when(aiModelPort.generateResponse(userMessage)).thenReturn("Chào bạn, tôi là stylist AI.");

        // When
        String response = interactor.handle(userMessage);

        // Then
        assertEquals("Chào bạn, tôi là stylist AI.", response);
        verify(productSearchService, never()).searchProductsSemantically(anyString(), anyInt());
    }
}
