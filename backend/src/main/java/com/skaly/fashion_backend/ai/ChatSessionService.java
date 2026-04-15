package com.skaly.fashion_backend.ai;

import com.skaly.fashion_backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatSessionService {

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;

    @Transactional
    public ChatSession createSession(UUID userId, String title) {
        User user = User.builder().id(userId).build();
        ChatSession session = ChatSession.builder()
                .user(user)
                .title(title)
                .isActive(true)
                .build();
        return sessionRepository.save(session);
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
