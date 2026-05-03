package com.skaly.fashion_backend.ai.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {

    List<ChatSession> findByUserIdOrderByUpdatedAtDesc(UUID userId);

    Page<ChatSession> findByUserIdAndIsActiveTrueOrderByUpdatedAtDesc(UUID userId, Pageable pageable);

    ChatSession findByUserIdAndIsActiveTrueOrderByIdDesc(UUID userId);
}
