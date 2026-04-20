package com.skaly.fashion_backend.recommendation.application;

import com.skaly.fashion_backend.recommendation.domain.model.FashionIntentResult;
import com.skaly.fashion_backend.recommendation.domain.model.RecommendedProduct;
import com.skaly.fashion_backend.recommendation.domain.port.AIModelPort;
import com.skaly.fashion_backend.recommendation.domain.port.SemanticProductSearchPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendProductInteractorUnitTest {

    @Mock
    private AIModelPort aiModelPort;

    @Mock
    private SemanticProductSearchPort semanticProductSearchPort;

    @InjectMocks
    private RecommendProductInteractor interactor;

    @Test
    void shouldRunRagWhenIntentIsProductDiscovery() {
        String userMessage = "Tìm cho tôi một bộ váy hoa";
        when(aiModelPort.interpretUserIntent(userMessage))
                .thenReturn(new FashionIntentResult(true, "váy hoa midi dự tiệc", List.of("váy", "hoa")));
        RecommendedProduct p = new RecommendedProduct(UUID.randomUUID(), "Váy hoa", "Midi", BigDecimal.valueOf(499000));
        when(semanticProductSearchPort.searchSimilarProducts(eq("váy hoa midi dự tiệc"), eq(5)))
                .thenReturn(List.of(p));
        when(aiModelPort.composeFashionAdvice(eq(userMessage), eq(List.of(p))))
                .thenReturn("Gợi ý: váy hoa midi phù hợp dự tiệc.");

        var response = interactor.execute(userMessage, 5);

        assertTrue(response.ragUsed());
        assertEquals(1, response.matchedProducts().size());
        assertEquals("Gợi ý: váy hoa midi phù hợp dự tiệc.", response.userVisibleMessage());
        verify(semanticProductSearchPort).searchSimilarProducts("váy hoa midi dự tiệc", 5);
    }

    @Test
    void shouldSkipVectorSearchWhenIntentIsGeneralChat() {
        String userMessage = "Chào bạn";
        when(aiModelPort.interpretUserIntent(userMessage)).thenReturn(FashionIntentResult.generalConversation());
        when(aiModelPort.completeChatPrompt(userMessage)).thenReturn("Chào bạn, tôi là stylist AI.");

        var response = interactor.execute(userMessage, 5);

        assertEquals("Chào bạn, tôi là stylist AI.", response.userVisibleMessage());
        assertTrue(response.matchedProducts().isEmpty());
        verify(semanticProductSearchPort, never()).searchSimilarProducts(anyString(), anyInt());
    }

    @Test
    void handleShouldDelegateToExecute() {
        when(aiModelPort.interpretUserIntent(anyString())).thenReturn(FashionIntentResult.generalConversation());
        when(aiModelPort.completeChatPrompt(anyString())).thenReturn("ok");

        assertEquals("ok", interactor.handle("x"));
    }
}
