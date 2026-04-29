package com.skaly.fashion_backend.ai.tryon.domain.port;

import java.util.UUID;

/**
 * Port for Try-On operations — decouples AI module from Try-On implementation.
 */
public interface TryOnPort {

    /**
     * Send a try-on request to the AI service (FastAPI) asynchronously.
     */
    void requestTryOn(UUID jobId, UUID userId, UUID productId, String userImageUrl);
}
