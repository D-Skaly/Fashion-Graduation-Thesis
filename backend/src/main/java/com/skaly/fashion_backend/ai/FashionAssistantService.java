package com.skaly.fashion_backend.ai;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FashionAssistantService {

    private static final int MAX_MESSAGE_LENGTH = 1000;

    private final ChatModel chatModel;
    private final boolean assistantEnabled;

    public FashionAssistantService(
            ObjectProvider<ChatModel> chatModelProvider,
            @Value("${application.ai.assistant.enabled:false}") boolean assistantEnabled) {
        this.chatModel = chatModelProvider.getIfAvailable();
        this.assistantEnabled = assistantEnabled;
    }

    public String chat(String message) {
        if (!assistantEnabled || chatModel == null) {
            throw new AiServiceUnavailableException(
                    "AI assistant is currently unavailable. Please configure AI_ASSISTANT_ENABLED=true and a valid GEMINI_API_KEY.");
        }

        String cleanedMessage = normalize(message);
        if (cleanedMessage.isBlank()) {
            throw new IllegalArgumentException("message must not be blank");
        }
        if (cleanedMessage.length() > MAX_MESSAGE_LENGTH) {
            throw new IllegalArgumentException("message exceeds maximum length of " + MAX_MESSAGE_LENGTH + " characters");
        }

        String prompt = "Bạn là stylist AI cho một shop thời trang cao cấp. " +
                "Trả lời ngắn gọn, thực tế, ưu tiên tư vấn phối đồ và gợi ý theo dịp sử dụng. " +
                "Nếu người dùng không nói rõ, hãy hỏi thêm tối đa 1 câu để làm rõ nhu cầu.\n\n" +
                "Yêu cầu người dùng: " + cleanedMessage;
        return chatModel.call(prompt);
    }

    private String normalize(String message) {
        return message == null ? "" : message.trim().replaceAll("\\s+", " ");
    }
}
