package com.skaly.fashion_backend.ai.tryon.application;

import com.skaly.fashion_backend.ai.domain.port.UserLookupPort;
import com.skaly.fashion_backend.ai.tryon.domain.JobStatus;
import com.skaly.fashion_backend.ai.tryon.domain.port.TryOnJob;
import com.skaly.fashion_backend.ai.tryon.domain.port.TryOnJobRepository;
import com.skaly.fashion_backend.ai.tryon.domain.port.TryOnPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Try-On 业务服务层 — 协调 TryOnPort、UserLookupPort、通知服务。
 * <p>
 * 不再直接依赖 UserRepository / UserEntity — 通过 {@link UserLookupPort} 校验用户存在性。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TryOnService {

    private final TryOnJobRepository tryOnJobRepository;
    private final UserLookupPort userLookupPort;
    private final TryOnPort tryOnPort;
    private final TryOnNotificationService notificationService;

    @Transactional
    public TryOnJob createJob(UUID userId, UUID productId, String userImageUrl) {
        // 1. 校验用户存在（通过 Port，解耦 User module）
        if (!userLookupPort.existsById(userId)) {
            throw new com.skaly.fashion_backend.common.domain.ResourceNotFoundException("User not found: " + userId);
        }

        TryOnJob job = TryOnJob.builder()
                .userId(userId)
                .productId(productId)
                .userImageUrl(userImageUrl)
                .status(JobStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        job = tryOnJobRepository.save(job);

        // 2. 发起 Try-On（Port 驱动，解耦具体实现）
        try {
            tryOnPort.requestTryOn(job.getId(), userId, productId, userImageUrl);
            log.info("Successfully initiated Try-On job {} via port", job.getId());
        } catch (Exception e) {
            log.error("Failed to initiate Try-On job", e);
            job.setStatus(JobStatus.FAILED);
            job.setErrorMessage("Failed to initiate AI processing: " + e.getMessage());
            tryOnJobRepository.save(job);
        }

        return job;
    }

    @Transactional
    public TryOnJob updateJobStatus(UUID jobId, JobStatus status, String resultImageUrl, String error) {
        TryOnJob job = tryOnJobRepository.findById(jobId)
                .orElseThrow(() -> new com.skaly.fashion_backend.common.domain.ResourceNotFoundException(
                        "Job not found: " + jobId));

        job.setStatus(status);
        if (resultImageUrl != null)
            job.setResultImageUrl(resultImageUrl);
        if (error != null)
            job.setErrorMessage(error);
        job.setUpdatedAt(LocalDateTime.now());

        TryOnJob saved = tryOnJobRepository.save(job);
        log.info("Updated Try-On job {} status to {}", jobId, status);

        // 3. 状态变更推送（通过通知服务解耦）
        if (status == JobStatus.COMPLETED || status == JobStatus.FAILED) {
            notificationService.notifyJobUpdate(job.getUserId(), saved);
        }

        return saved;
    }

    public Optional<TryOnJob> findById(UUID jobId) {
        return tryOnJobRepository.findById(jobId);
    }
}
