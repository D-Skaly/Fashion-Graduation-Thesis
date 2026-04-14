package com.skaly.fashion_backend.ai;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "spring.ai.google.genai.api-key")
public class FashionAssistantService {

    private final ChatModel chatModel;

    public FashionAssistantService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String chat(String message) {
        String prompt = "Bạn là stylist AI cho một shop thời trang cao cấp. " +
                "Trả lời ngắn gọn, thực tế, ưu tiên tư vấn phối đồ và gợi ý theo dịp sử dụng. " +
                "Nếu người dùng không nói rõ, hãy hỏi thêm tối đa 1 câu để làm rõ nhu cầu.\n\n" +
                "Yêu cầu người dùng: " + message;
        return chatModel.call(prompt);
    }
}
