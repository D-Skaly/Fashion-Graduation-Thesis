package com.skaly.fashion_backend.ai.tryon.interfaces;

import com.skaly.fashion_backend.ai.tryon.application.TryOnService;
import com.skaly.fashion_backend.ai.tryon.application.TryOnNotificationService;
import com.skaly.fashion_backend.ai.tryon.domain.JobStatus;
import com.skaly.fashion_backend.ai.tryon.domain.port.TryOnJob;
import com.skaly.fashion_backend.ai.tryon.domain.port.TryOnJobRepository;
import com.skaly.fashion_backend.common.domain.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tryon")
@RequiredArgsConstructor
public class TryOnController {

    private final TryOnService tryOnService;
    private final TryOnNotificationService notificationService;

    @PostMapping
    public ResponseEntity<ApiResponse<TryOnJob>> createJob(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam UUID productId,
            @RequestParam(required = false) String userImageUrl) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(tryOnService.createJob(userId, productId, userImageUrl)));
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<ApiResponse<TryOnJob>> getJob(@PathVariable UUID jobId) {
        return tryOnService.findById(jobId)
                .map(job -> ResponseEntity.ok(ApiResponse.success(job)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/stream", produces = org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE)
    public org.springframework.web.servlet.mvc.method.annotation.SseEmitter subscribe(
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        return notificationService.subscribe(userId);
    }

    // Callback for orchestrator
    @PostMapping("/callback")
    public ResponseEntity<Void> callback(@RequestBody Map<String, Object> payload) {
        UUID jobId = UUID.fromString((String) payload.get("jobId"));
        String statusStr = (String) payload.get("status");
        String resultImageUrl = (String) payload.get("resultImageUrl");
        String error = (String) payload.get("error");

        JobStatus status = JobStatus.valueOf(statusStr);
        TryOnJob updatedJob = tryOnService.updateJobStatus(jobId, status, resultImageUrl, error);

        // Push notification to user
        notificationService.notifyJobUpdate(updatedJob.getUserId(), updatedJob);

        return ResponseEntity.ok().build();
    }
}
