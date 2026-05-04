package com.skaly.fashion_backend.ai.application;

import com.skaly.fashion_backend.ai.domain.port.AIModelPort;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.skaly.fashion_backend.common.domain.AiServiceUnavailableException;
import reactor.core.publisher.Flux;

import java.util.UUID;
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
    private final ChatSessionService chatSessionService;
    private final FactCheckingEvaluator factChecker;
    private final RelevancyEvaluator relevancyEvaluator;

    public FashionAssistantService(
            AIModelPort aiModelPort,
            AiAssistantProperties properties,
            MeterRegistry meterRegistry,
            ChatSessionService chatSessionService,
            FactCheckingEvaluator factChecker,
            RelevancyEvaluator relevancyEvaluator) {
        this.aiModelPort = aiModelPort;
        this.properties = properties;
        this.chatSessionService = chatSessionService;
        this.factChecker = factChecker;
        this.relevancyEvaluator = relevancyEvaluator;
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
        String prompt = preparePrompt(message, sessionId, maxHistoryMessages);

        try {
            String answer = aiModelPort.completeChatPrompt(prompt);
            
            // Run evaluators
            FactCheckingEvaluator.EvaluationResult factCheck = factChecker.evaluate(message, answer);
            if (!factCheck.passed()) {
                log.warn("Fact check failed: {}", factCheck.reason());
            }
            
            double relevancy = relevancyEvaluator.evaluateRelevancy(message, answer);
            log.info("Relevancy score: {} for message: {}", relevancy, message);
            
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

    public Flux<String> chatStream(String message, UUID sessionId, int maxHistoryMessages) {
        String prompt = preparePrompt(message, sessionId, maxHistoryMessages);
        return aiModelPort.streamChatPrompt(prompt)
                .doOnComplete(successCounter::increment)
                .doOnError(t -> {
                    failureCounter.increment();
                    log.error("event=ai_chat_stream_failure reason={}", t.getMessage());
                });
    }

    private String preparePrompt(String message, UUID sessionId, int maxHistoryMessages) {
        if (!properties.isEnabled() || aiModelPort == null) {
            unavailableCounter.increment();
            throw new AiServiceUnavailableException(
                    "AI assistant is currently unavailable. Please configure AI_ASSISTANT_ENABLED=true and a valid API key.");
        }

        String cleanedMessage = normalize(message);
        if (cleanedMessage.isBlank()) {
            throw new IllegalArgumentException("message must not be blank");
        }
        if (cleanedMessage.length() > properties.getMaxMessageLength()) {
            throw new IllegalArgumentException(
                    "message exceeds maximum length of " + properties.getMaxMessageLength() + " characters");
        }

        String context = "";
        if (sessionId != null && maxHistoryMessages > 0) {
            context = chatSessionService.buildContextFromHistory(sessionId, maxHistoryMessages);
        }

        return buildPromptWithContext(cleanedMessage, context);
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
        // TODO: trigger re-indexing if needed
    }
}