package com.skaly.fashion_backend.ai.interfaces;

import com.skaly.fashion_backend.ai.application.FashionAssistantService;
import com.skaly.fashion_backend.ai.application.SizeRecommendationService;
import com.skaly.fashion_backend.ai.interfaces.dto.AiChatRequest;
import com.skaly.fashion_backend.ai.interfaces.dto.AiChatResponse;
import com.skaly.fashion_backend.common.domain.ApiResponse;
import com.skaly.fashion_backend.recommendation.application.RecommendProductInteractor;
import com.skaly.fashion_backend.recommendation.domain.model.ProductRecommendationResponse;
import com.skaly.fashion_backend.recommendation.domain.model.RecommendedProduct;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
@Validated
public class AiController {

    private final FashionAssistantService fashionAssistantService;
    private final SizeRecommendationService sizeRecommendationService;
    private final RecommendProductInteractor recommendProductInteractor;

    public AiController(FashionAssistantService fashionAssistantService, 
                        SizeRecommendationService sizeRecommendationService,
                        RecommendProductInteractor recommendProductInteractor) {
        this.fashionAssistantService = fashionAssistantService;
        this.sizeRecommendationService = sizeRecommendationService;
        this.recommendProductInteractor = recommendProductInteractor;
    }

    @GetMapping("/recommend-size/{productId}")
    public ResponseEntity<ApiResponse<SizeRecommendationService.SizeRecommendationResponse>> recommendSize(
            @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails,
            @org.springframework.web.bind.annotation.PathVariable java.util.UUID productId) {
        java.util.UUID userId = java.util.UUID.fromString(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(sizeRecommendationService.recommendSize(userId, productId)));
    }

    @GetMapping("/chat")
    public ResponseEntity<ApiResponse<AiChatResponse>> chat(@RequestParam @NotBlank String message) {
        String answer = fashionAssistantService.chat(message);
        return ResponseEntity.ok(ApiResponse.success(new AiChatResponse(answer, null)));
    }

    @PostMapping("/chat")
    public ResponseEntity<ApiResponse<AiChatResponse>> chat(@Valid @RequestBody AiChatRequest request) {
        String answer = fashionAssistantService.chat(request.getMessage());
        return ResponseEntity.ok(ApiResponse.success(new AiChatResponse(answer, request.getSessionId())));
    }

    @PostMapping(value = "/chat/stream", produces = org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE)
    public reactor.core.publisher.Flux<String> streamChat(@Valid @RequestBody AiChatRequest request) {
        return fashionAssistantService.chatStream(request.getMessage(), null, 0);
    }

    @PostMapping("/reindex")
    public ResponseEntity<ApiResponse<String>> reindex() {
        fashionAssistantService.reindex();
        return ResponseEntity.ok(ApiResponse.success("Re-indexing started"));
    }

    @GetMapping("/recommendations")
    public ResponseEntity<ApiResponse<List<RecommendedProduct>>> getRecommendations(
            @RequestParam(defaultValue = "5") int limit) {
        ProductRecommendationResponse response = recommendProductInteractor.execute("gợi ý sản phẩm cho tôi", limit);
        return ResponseEntity.ok(ApiResponse.success(response.matchedProducts()));
    }
}
