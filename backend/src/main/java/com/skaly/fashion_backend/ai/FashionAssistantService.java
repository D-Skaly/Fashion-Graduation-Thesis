package com.skaly.fashion_backend.ai;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class FashionAssistantService {

    private final ChatModel chatModel;
    private final AiAssistantProperties properties;
    private final Timer latencyTimer;
    private final Counter successCounter;
    private final Counter failureCounter;
    private final Counter unavailableCounter;
    private final com.skaly.fashion_backend.product.ProductEmbeddingService productEmbeddingService;
    private final ChatSessionService chatSessionService;

    public FashionAssistantService(
            ObjectProvider<ChatModel> chatModelProvider,
            AiAssistantProperties properties,
            MeterRegistry meterRegistry,
            com.skaly.fashion_backend.product.ProductEmbeddingService productEmbeddingService,
            ChatSessionService chatSessionService) {
        this.chatModel = chatModelProvider.getIfAvailable();
        this.properties = properties;
        this.productEmbeddingService = productEmbeddingService;
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

        if (!properties.enabled() || chatModel == null) {
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

        String prompt = buildPromptWithContext(cleanedMessage, context);

        try {
            String answer = invokeModelWithRetry(prompt);
            successCounter.increment();
            latencyTimer.record(System.nanoTime() - startNs, TimeUnit.NANOSECONDS);
            log.info("event=ai_chat_success attempts={} latency_ms={}", properties.retry().maxAttempts(),
                    Duration.ofNanos(System.nanoTime() - startNs).toMillis());
            return answer;
        } catch (RuntimeException ex) {
            failureCounter.increment();
            latencyTimer.record(System.nanoTime() - startNs, TimeUnit.NANOSECONDS);
            log.warn("event=ai_chat_failure reason={} latency_ms={}", ex.getMessage(),
                    Duration.ofNanos(System.nanoTime() - startNs).toMillis());
            throw ex;
        }
    }

    private String invokeModelWithRetry(String prompt) {
        RuntimeException lastError = null;

        for (int attempt = 1; attempt <= properties.retry().maxAttempts(); attempt++) {
            try {
                return callModelWithTimeout(prompt);
            } catch (RuntimeException ex) {
                lastError = ex;
                if (attempt == properties.retry().maxAttempts()) {
                    break;
                }

                try {
                    Thread.sleep(properties.retry().backoffMs());
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted during AI retry backoff", interruptedException);
                }
            }
        }

        throw new RuntimeException("AI chat failed after retries", lastError);
    }

    private String callModelWithTimeout(String prompt) {
        try {
            return CompletableFuture.supplyAsync(() -> chatModel.call(prompt))
                    .orTimeout(properties.timeout().responseMs(), TimeUnit.MILLISECONDS)
                    .join();
        } catch (RuntimeException ex) {
            throw new RuntimeException("AI model invocation failed or timed out", ex);
        }
    }

    private String buildPromptWithContext(String cleanedMessage, String context) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Bạn là stylist AI cho một shop thời trang cao cấp. ");
        prompt.append("Trả lời ngắn gọn, thực tế, ưu tiên tư vấn phối đồ và gợi ý theo dịp sử dụng. ");
        prompt.append("Nếu người dùng không nói rõ, hãy hỏi thêm tối đa 1 câu để làm rõ nhu cầu.\n\n");

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
