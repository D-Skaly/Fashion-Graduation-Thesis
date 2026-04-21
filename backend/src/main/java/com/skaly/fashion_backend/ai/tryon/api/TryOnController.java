package com.skaly.fashion_backend.ai.tryon.api;

import com.skaly.fashion_backend.ai.tryon.application.TryOnService;
import com.skaly.fashion_backend.ai.tryon.JobStatus;
import com.skaly.fashion_backend.ai.tryon.TryOnJob;
import com.skaly.fashion_backend.ai.tryon.TryOnJobRepository;
import com.skaly.fashion_backend.common.ApiResponse;
import com.skaly.fashion_backend.user.infrastructure.persistence.entities.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tryon")
@RequiredArgsConstructor
public class TryOnController {

    private final TryOnService tryOnService;
    private final TryOnJobRepository tryOnJobRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<TryOnJob>> createJob(
            @AuthenticationPrincipal UserEntity user,
            @RequestParam UUID productId,
            @RequestParam(required = false) String userImageUrl) {
        return ResponseEntity.ok(ApiResponse.success(tryOnService.createJob(user.getId(), productId, userImageUrl)));
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<ApiResponse<TryOnJob>> getJob(@PathVariable UUID jobId) {
        return tryOnJobRepository.findById(jobId)
                .map(job -> ResponseEntity.ok(ApiResponse.success(job)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Callback for orchestrator
    @PostMapping("/callback")
    public ResponseEntity<Void> callback(@RequestBody Map<String, Object> payload) {
        UUID jobId = UUID.fromString((String) payload.get("jobId"));
        String statusStr = (String) payload.get("status");
        String resultImageUrl = (String) payload.get("resultImageUrl");
        String error = (String) payload.get("error");

        JobStatus status = JobStatus.valueOf(statusStr);
        tryOnService.updateJobStatus(jobId, status, resultImageUrl, error);
        
        return ResponseEntity.ok().build();
    }
}

