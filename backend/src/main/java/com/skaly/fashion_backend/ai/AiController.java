package com.skaly.fashion_backend.ai;

import com.skaly.fashion_backend.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
@ConditionalOnProperty(name = "spring.ai.google.genai.api-key")
public class AiController {

    private final FashionAssistantService fashionAssistantService;

    public AiController(FashionAssistantService fashionAssistantService) {
        this.fashionAssistantService = fashionAssistantService;
    }

    @GetMapping("/chat")
    public ResponseEntity<ApiResponse<AiChatResponse>> chat(@RequestParam String message) {
        String answer = fashionAssistantService.chat(message);
        return ResponseEntity.ok(ApiResponse.success(new AiChatResponse(answer)));
    }

    @PostMapping("/chat")
    public ResponseEntity<ApiResponse<AiChatResponse>> chat(@Valid @RequestBody AiChatRequest request) {
        String answer = fashionAssistantService.chat(request.message());
        return ResponseEntity.ok(ApiResponse.success(new AiChatResponse(answer)));
    }
}
