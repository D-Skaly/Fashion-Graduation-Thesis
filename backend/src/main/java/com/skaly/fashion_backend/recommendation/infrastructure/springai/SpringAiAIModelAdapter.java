package com.skaly.fashion_backend.recommendation.infrastructure.springai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skaly.fashion_backend.recommendation.domain.model.FashionIntentResult;
import com.skaly.fashion_backend.recommendation.domain.model.RecommendedProduct;
import com.skaly.fashion_backend.recommendation.domain.port.AIModelPort;
import org.springframework.ai.chat.model.ChatModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * Adapter dựa trên Spring AI {@link ChatModel} (thường cấu hình Gemini qua spring.ai.google.genai).
 * Dùng cho môi trường dev/test khi không bật {@code application.gemini.rest.enabled}.
 */
public class SpringAiAIModelAdapter implements AIModelPort {

    private final ChatModel chatModel;
    private final ObjectMapper objectMapper;

    public SpringAiAIModelAdapter(ChatModel chatModel, ObjectMapper objectMapper) {
        this.chatModel = chatModel;
        this.objectMapper = objectMapper;
    }

    @Override
    public FashionIntentResult interpretUserIntent(String userMessage) {
        String prompt = """
                Bạn là bộ phân loại ý định. Chỉ trả về JSON hợp lệ:
                {"productDiscovery":boolean,"retrievalQueryText":string,"extractedKeywords":string[]}
                productDiscovery=true nếu người dùng muốn mua/tìm/gợi ý sản phẩm thời trang.
                Câu người dùng: %s
                """.formatted(userMessage);
        try {
            String raw = chatModel.call(prompt).trim();
            return parseIntent(raw, userMessage);
        } catch (Exception ex) {
            return FashionIntentResult.generalConversation();
        }
    }

    @Override
    public String completeChatPrompt(String composedPrompt) {
        if (chatModel == null) {
            return "";
        }
        return chatModel.call(composedPrompt);
    }

    @Override
    public String composeFashionAdvice(String userMessage, List<RecommendedProduct> candidates) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < candidates.size(); i++) {
            RecommendedProduct p = candidates.get(i);
            sb.append(i + 1).append(". ").append(p.name()).append(" — ").append(p.basePrice()).append("\n");
        }
        String prompt = """
                Bạn là stylist. Tư vấn ngắn gọn tiếng Việt dựa trên sản phẩm sau và nhu cầu người dùng.

                Sản phẩm:
                %s

                Nhu cầu: %s
                """.formatted(sb, userMessage);
        return completeChatPrompt(prompt);
    }

    private FashionIntentResult parseIntent(String raw, String userMessage) {
        try {
            // Model đôi khi bọc JSON trong ``` — lấy phần giữa nếu có
            String json = stripMarkdownFence(raw);
            JsonNode root = objectMapper.readTree(json);
            boolean discovery = root.path("productDiscovery").asBoolean(false);
            String retrieval = root.path("retrievalQueryText").asText("").trim();
            List<String> keywords = new ArrayList<>();
            JsonNode kw = root.path("extractedKeywords");
            if (kw.isArray()) {
                StreamSupport.stream(kw.spliterator(), false)
                        .map(JsonNode::asText)
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .forEach(keywords::add);
            }
            if (discovery && retrieval.isEmpty()) {
                retrieval = userMessage;
            }
            return new FashionIntentResult(discovery, retrieval, List.copyOf(keywords));
        } catch (Exception e) {
            return FashionIntentResult.generalConversation();
        }
    }

    private static String stripMarkdownFence(String raw) {
        String t = raw.trim();
        if (t.startsWith("```")) {
            int first = t.indexOf('\n');
            int last = t.lastIndexOf("```");
            if (first > 0 && last > first) {
                return t.substring(first + 1, last).trim();
            }
        }
        return t;
    }
}
