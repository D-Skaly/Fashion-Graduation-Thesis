package com.skaly.fashion_backend.ai.infrastructure;

import com.skaly.fashion_backend.ai.domain.AIModelPort;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

@Component
public class SpringAIChatAdapter implements AIModelPort {

    private final ChatModel chatModel;

    public SpringAIChatAdapter(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public String generateResponse(String prompt) {
        if (chatModel == null) {
            return "AI service is currently unavailable.";
        }
        return chatModel.call(prompt);
    }
}
