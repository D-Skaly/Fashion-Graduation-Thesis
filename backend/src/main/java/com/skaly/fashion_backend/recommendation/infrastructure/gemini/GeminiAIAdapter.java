package com.skaly.fashion_backend.recommendation.infrastructure.gemini;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skaly.fashion_backend.recommendation.domain.model.FashionIntentResult;
import com.skaly.fashion_backend.recommendation.domain.model.RecommendedProduct;
import com.skaly.fashion_backend.recommendation.domain.port.AIModelPort;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * Adapter Infrastructure: map hợp đồng {@link AIModelPort} sang HTTP Gemini ({@code :generateContent}).
 * <p>
 * Toàn bộ chi tiết JSON request/response của Google nằm ở đây — Domain không biết URL hay schema REST.
 */
public class GeminiAIAdapter implements AIModelPort {

    private static final String BASE_URL = "https://generativelanguage.googleapis.com";

    private final GeminiRestProperties properties;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public GeminiAIAdapter(GeminiRestProperties properties, RestClient restClient, ObjectMapper objectMapper) {
        this.properties = properties;
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public FashionIntentResult interpretUserIntent(String userMessage) {
        String system = """
                Bạn là bộ phân loại ý định cho shop thời trang. Chỉ trả về một JSON hợp lệ, không markdown, không giải thích thêm.
                Schema:
                {"productDiscovery":boolean,"retrievalQueryText":string,"extractedKeywords":string[]}
                - productDiscovery=true khi người dùng muốn tìm/gợi ý sản phẩm, outfit, size, màu sắc mua sắm.
                - retrievalQueryText: một câu tiếng Việt giàu ngữ nghĩa để embedding tìm sản phẩm (tóm tắt nhu cầu).
                - extractedKeywords: 3-8 từ khóa ngắn.
                """;

        String prompt = system + "\nCâu người dùng:\n" + userMessage;
        try {
            String raw = generateContentPlainJson(prompt, true);
            return parseIntentJson(raw, userMessage);
        } catch (RestClientException | IllegalArgumentException ex) {
            return FashionIntentResult.generalConversation();
        }
    }

    @Override
    public String completeChatPrompt(String composedPrompt) {
        return generateContentPlainJson(composedPrompt, false).trim();
    }

    @Override
    public String composeFashionAdvice(String userMessage, List<RecommendedProduct> candidates) {
        StringBuilder catalog = new StringBuilder();
        for (int i = 0; i < candidates.size(); i++) {
            RecommendedProduct p = candidates.get(i);
            catalog.append(i + 1)
                    .append(". ")
                    .append(p.name())
                    .append(" — ")
                    .append(p.description() == null ? "" : p.description())
                    .append(" (giá: ")
                    .append(p.basePrice())
                    .append(")\n");
        }
        String composed = """
                Bạn là stylist. Dựa trên danh sách sản phẩm sau từ cửa hàng, tư vấn ngắn gọn (tối đa 8 câu), thực tế, tiếng Việt.
                Ưu tiên gợi ý phối đồ với các mặt hàng có sẵn. Nếu mô tả sản phẩm thiếu, hãy nói rõ giả định của bạn.

                Danh sách sản phẩm:
                %s

                Yêu cầu người dùng: %s
                """.formatted(catalog, userMessage);
        return completeChatPrompt(composed);
    }

    private String generateContentPlainJson(String textPrompt, boolean jsonMimeType) {
        String path = "/v1beta/models/%s:generateContent".formatted(properties.model());
        String uri = path + "?key=" + properties.apiKey();

        String body = buildRequestBody(textPrompt, jsonMimeType);

        String response = restClient.post()
                .uri(BASE_URL + uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);

        return extractTextFromGenerateContentResponse(response);
    }

    private String buildRequestBody(String textPrompt, boolean jsonMimeType) {
        try {
            var root = objectMapper.createObjectNode();
            var contents = root.putArray("contents");
            var content = contents.addObject();
            var parts = content.putArray("parts");
            parts.addObject().put("text", textPrompt);
            var gen = root.putObject("generationConfig");
            gen.put("temperature", 0.25);
            if (jsonMimeType) {
                gen.put("responseMimeType", "application/json");
            }
            return objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to build Gemini request JSON", e);
        }
    }

    private FashionIntentResult parseIntentJson(String rawJson, String fallbackUserMessage) {
        try {
            JsonNode root = objectMapper.readTree(rawJson);
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
                retrieval = fallbackUserMessage;
            }
            return new FashionIntentResult(discovery, retrieval, List.copyOf(keywords));
        } catch (Exception e) {
            return FashionIntentResult.generalConversation();
        }
    }

    private String extractTextFromGenerateContentResponse(String responseJson) {
        try {
            JsonNode root = objectMapper.readTree(responseJson);
            JsonNode candidates = root.path("candidates");
            if (!candidates.isArray() || candidates.isEmpty()) {
                return "";
            }
            JsonNode parts = candidates.get(0).path("content").path("parts");
            if (!parts.isArray() || parts.isEmpty()) {
                return "";
            }
            return parts.get(0).path("text").asText("");
        } catch (Exception e) {
            throw new IllegalArgumentException("Unparseable Gemini response", e);
        }
    }
}
