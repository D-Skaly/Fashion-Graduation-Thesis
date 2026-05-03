package com.skaly.fashion_backend.ai.application;

import com.skaly.fashion_backend.ai.domain.port.UserLookupPort;
import com.skaly.fashion_backend.recommendation.domain.port.AIModelPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Quản lý phiên chat và tin nhắn. Mọi gọi LLM cho nội dung hội thoại phải đi
 * qua {@link AIModelPort}
 * — ví dụ luồng hoàn chỉnh: {@link FashionAssistantService} ghép context từ
 * lịch sử rồi gọi {@code AIModelPort.completeChatPrompt}.
 * <p>
 * Không phụ thuộc trực tiếp JPA entity hay repository của User module — toàn bộ
 * giao tiếp qua {@link UserLookupPort}.
 */
@Service
@RequiredArgsConstructor
public class ChatSessionService {

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;
    private final UserLookupPort userLookupPort;
    private final AIModelPort aiModelPort;

    @Transactional
    public ChatSession createSession(UUID userId, String title) {
        if (!userLookupPort.existsById(userId)) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        ChatSession session = ChatSession.builder()
                .userId(userId)
                .title(title)
                .isActive(true)
                .build();
        return sessionRepository.save(session);
    }

    /**
     * Gợi ý tiêu đề phiên (DRAFT) — chỉ dùng kết quả LLM làm đề xuất, không tự động
     * ghi DB production nếu chưa có bước phê duyệt.
     */
    @Transactional(readOnly = true)
    public String suggestSessionTitleDraft(String userMessageSnippet) {
        if (userMessageSnippet == null || userMessageSnippet.isBlank()) {
            return "";
        }
        String composed = "Suggest a very short chat session title (max 6 words) for this user message. Reply with title only.\n\n"
                + userMessageSnippet;
        return aiModelPort.completeChatPrompt(composed);
    }

    @Transactional
    public ChatSession getOrCreateActiveSession(UUID userId) {
        ChatSession activeSession = sessionRepository.findByUserIdAndIsActiveTrueOrderByIdDesc(userId);
        if (activeSession == null) {
            return createSession(userId, "New Chat");
        }
        return activeSession;
    }

    @Transactional(readOnly = true)
    public ChatSession getSession(UUID sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Chat session not found"));
    }

    @Transactional(readOnly = true)
    public Page<ChatSession> getUserSessions(UUID userId, Pageable pageable) {
        return sessionRepository.findByUserIdAndIsActiveTrueOrderByUpdatedAtDesc(userId, pageable);
    }

    @Transactional
    public void addMessage(UUID sessionId, String content, String role) {
        ChatSession session = getSession(sessionId);
        ChatMessage message = ChatMessage.builder()
                .session(session)
                .content(content)
                .role(role)
                .build();
        messageRepository.save(message);
        sessionRepository.save(session);
    }

    @Transactional(readOnly = true)
    public List<ChatMessage> getSessionMessages(UUID sessionId) {
        return messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
    }

    @Transactional
    public void updateSessionTitle(UUID sessionId, String title) {
        ChatSession session = getSession(sessionId);
        session.setTitle(title);
        sessionRepository.save(session);
    }

    @Transactional
    public void deactivateSession(UUID sessionId) {
        ChatSession session = getSession(sessionId);
        session.setIsActive(false);
        sessionRepository.save(session);
    }

    @Transactional
    public void deleteSession(UUID sessionId) {
        ChatSession session = getSession(sessionId);
        sessionRepository.delete(session);
    }

    @Transactional(readOnly = true)
    public String buildContextFromHistory(UUID sessionId, int maxMessages) {
        List<ChatMessage> messages = getSessionMessages(sessionId);
        if (messages.isEmpty()) {
            return "";
        }

        StringBuilder context = new StringBuilder();
        int count = 0;
        for (int i = messages.size() - 1; i >= 0 && count < maxMessages; i--) {
            ChatMessage msg = messages.get(i);
            context.append(msg.getRole()).append(": ").append(msg.getContent()).append("\n");
            count++;
        }

        return context.toString();
    }
}
