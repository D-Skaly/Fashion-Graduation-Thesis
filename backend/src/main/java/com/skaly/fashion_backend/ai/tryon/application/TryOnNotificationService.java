package com.skaly.fashion_backend.ai.tryon.application;

import com.skaly.fashion_backend.ai.tryon.domain.port.TryOnJob;
import com.skaly.fashion_backend.ai.tryon.domain.port.TryOnJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SSE 通知服务，用于 Try-On 状态实时推送。
 * <p>
 * 独立服务，避免 TryOnService 耦合通知逻辑。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TryOnNotificationService {

    private final Map<UUID, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(UUID userId) {
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L); // 30 minutes timeout

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError((e) -> emitters.remove(userId));

        emitters.put(userId, emitter);

        // Send initial connection event
        try {
            emitter.send(SseEmitter.event()
                    .name("INIT")
                    .data("Connected to Try-On notification service"));
        } catch (IOException e) {
            log.error("Error sending initial SSE event for user {}", userId, e);
        }

        return emitter;
    }

    @Async
    public void notifyJobUpdate(UUID userId, TryOnJob job) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("TRY_ON_UPDATE")
                        .data(job));
                log.info("Successfully pushed Try-On update to user {}", userId);
            } catch (IOException e) {
                log.error("Error pushing SSE update to user {}", userId, e);
                emitters.remove(userId);
            }
        } else {
            log.debug("No active SSE connection for user {}, skip notification", userId);
        }
    }
}
