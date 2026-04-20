package com.skaly.fashion_backend.ai;

import com.skaly.fashion_backend.ai.domain.AIModelPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class FashionAssistantService {

    private final AIModelPort aiModelPort;
    private final AiAssistantProperties properties;
    private final Timer latencyTimer;
    private final Counter successCounter;
    private final Counter failureCounter;
    private final Counter unavailableCounter;
    private final com.skaly.fashion_backend.product.ProductEmbeddingService productEmbeddingService;
    private final com.skaly.fashion_backend.product.ProductRepository productRepository;
    private final ChatSessionService chatSessionService;

    public FashionAssistantService(
            AIModelPort aiModelPort,
            AiAssistantProperties properties,
            MeterRegistry meterRegistry,
            com.skaly.fashion_backend.product.ProductEmbeddingService productEmbeddingService,
            com.skaly.fashion_backend.product.ProductRepository productRepository,
            ChatSessionService chatSessionService) {
        this.aiModelPort = aiModelPort;
        this.properties = properties;
        this.productEmbeddingService = productEmbeddingService;
        this.productRepository = productRepository;
        this.chatSessionService = chatSessionService;
        this.latencyTimer = Timer.builder("ai.chat.latency")
                .description("Latency for AI chat completion")
                .register(meterRegistry);
        this.successCounter = Counter.builder("ai.chat.requests")
                .tag("status", "success")
                .register(meterRegistry);
        this.failureCounter = Counter.builder("ai.chat.requests")
                .tag("status", "failure")
                .register(meterRegistry);
        this.unavailableCounter = Counter.builder("ai.chat.requests")
                .tag("status", "unavailable")
                .register(meterRegistry);
    }

    public String chat(String message) {
        return chatWithContext(message, null, 0);
    }

    public String chatWithContext(String message, UUID sessionId, int maxHistoryMessages) {
        long startNs = System.nanoTime();

        if (!properties.enabled() || aiModelPort == null) {
            unavailableCounter.increment();
            throw new AiServiceUnavailableException(
                    "AI assistant is currently unavailable. Please configure AI_ASSISTANT_ENABLED=true and a valid GEMINI_API_KEY.");
        }

        String cleanedMessage = normalize(message);
        if (cleanedMessage.isBlank()) {
            throw new IllegalArgumentException("message must not be blank");
        }
        if (cleanedMessage.length() > properties.maxMessageLength()) {
            throw new IllegalArgumentException(
                    "message exceeds maximum length of " + properties.maxMessageLength() + " characters");
        }

        String context = "";
        if (sessionId != null && maxHistoryMessages > 0) {
            context = chatSessionService.buildContextFromHistory(sessionId, maxHistoryMessages);
        }

        // RAG: Tìm kiếm sản phẩm liên quan
        String productContext = "";
        try {
            float[] queryVector = productEmbeddingService.embedQuery(cleanedMessage);
            var products = productRepository.findTopKByEmbeddingVectorClosestTo(queryVector, 5);
            if (!products.isEmpty()) {
                StringBuilder sb = new StringBuilder("Dưới đây là một số sản phẩm liên quan từ cửa hàng của chúng tôi:\n");
                for (var p : products) {
                    sb.append(String.format("- %s (Giá: %s): %s\n", p.getName(), p.getBasePrice(), p.getDescription()));
                }
                productContext = sb.toString();
            }
        } catch (Exception e) {
            log.warn("Failed to fetch product context for RAG: {}", e.getMessage());
        }

        String prompt = buildPromptWithContext(cleanedMessage, context, productContext);

        try {
            String answer = executeChat(prompt).join();
            successCounter.increment();
            latencyTimer.record(System.nanoTime() - startNs, TimeUnit.NANOSECONDS);
            return answer;
        } catch (Exception ex) {
            failureCounter.increment();
            latencyTimer.record(System.nanoTime() - startNs, TimeUnit.NANOSECONDS);
            log.error("event=ai_chat_failure reason={}", ex.getMessage());
            throw ex;
        }
    }

    @CircuitBreaker(name = "aiAssistant", fallbackMethod = "chatFallback")
    @Retry(name = "aiAssistant")
    @TimeLimiter(name = "aiAssistant")
    public CompletableFuture<String> executeChat(String prompt) {
        return CompletableFuture.supplyAsync(() -> aiModelPort.generateResponse(prompt));
    }

    public CompletableFuture<String> chatFallback(String prompt, Throwable t) {
        log.warn("event=ai_chat_fallback reason={} prompt_preview={}", 
                t.getMessage(), 
                prompt.substring(0, Math.min(prompt.length(), 50)));
        return CompletableFuture.completedFuture(
                "Xin lỗi, tôi đang gặp khó khăn khi kết nối với hệ thống AI. Vui lòng thử lại sau giây lát.");
    }

    private String buildPromptWithContext(String cleanedMessage, String context, String productContext) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Bạn là stylist AI cho một shop thời trang cao cấp. ");
        prompt.append("Trả lời ngắn gọn, thực tế, ưu tiên tư vấn phối đồ và gợi ý theo dịp sử dụng. ");
        prompt.append("Nếu người dùng không nói rõ, hãy hỏi thêm tối đa 1 câu để làm rõ nhu cầu.\n\n");

        if (!productContext.isEmpty()) {
            prompt.append("Thông tin sản phẩm có sẵn:\n");
            prompt.append(productContext);
            prompt.append("\n");
        }

        if (!context.isEmpty()) {
            prompt.append("Lịch sử trò chuyện gần đây:\n");
            prompt.append(context);
            prompt.append("\n");
        }

        prompt.append("Yêu cầu hiện tại: ");
        prompt.append(cleanedMessage);

        return prompt.toString();
    }

    private String normalize(String message) {
        return message == null ? "" : message.trim().replaceAll("\\s+", " ");
    }

    public void reindex() {
        productEmbeddingService.generateEmbeddingsForAllMissing();
    }
}
