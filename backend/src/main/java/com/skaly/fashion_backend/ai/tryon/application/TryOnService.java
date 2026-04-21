package com.skaly.fashion_backend.ai.tryon.application;

import com.skaly.fashion_backend.ai.tryon.JobStatus;
import com.skaly.fashion_backend.ai.tryon.TryOnJob;
import com.skaly.fashion_backend.ai.tryon.TryOnJobRepository;
import com.skaly.fashion_backend.user.domain.entities.User;
import com.skaly.fashion_backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TryOnService {

    private final TryOnJobRepository tryOnJobRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${application.ai.orchestrator.url:http://localhost:3001}")
    private String orchestratorUrl;

    @Transactional
    public TryOnJob createJob(UUID userId, UUID productId, String userImageUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TryOnJob job = TryOnJob.builder()
                .userId(userId)
                .productId(productId)
                .userImageUrl(userImageUrl)
                .status(JobStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        job = tryOnJobRepository.save(job);

        // Enqueue to orchestrator
        try {
            String url = orchestratorUrl + "/api/v1/ai/tryon";
            Map<String, String> payload = Map.of(
                    "userId", userId.toString(),
                    "productId", productId.toString(),
                    "userImageUrl", userImageUrl != null ? userImageUrl : "",
                    "jobId", job.getId().toString()
            );
            restTemplate.postForEntity(url, payload, Map.class);
            log.info("Successfully enqueued Try-On job {} to orchestrator", job.getId());
        } catch (Exception e) {
            log.error("Failed to enqueue Try-On job to orchestrator", e);
            job.setStatus(JobStatus.FAILED);
            job.setErrorMessage("Failed to connect to AI orchestrator: " + e.getMessage());
            tryOnJobRepository.save(job);
        }

        return job;
    }

    @Transactional
    public void updateJobStatus(UUID jobId, JobStatus status, String resultImageUrl, String error) {
        TryOnJob job = tryOnJobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        job.setStatus(status);
        if (resultImageUrl != null) job.setResultImageUrl(resultImageUrl);
        if (error != null) job.setErrorMessage(error);
        
        tryOnJobRepository.save(job);
        log.info("Updated Try-On job {} status to {}", jobId, status);
    }
}
