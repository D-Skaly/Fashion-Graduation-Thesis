package com.skaly.fashion_backend.ai.tryon.application;

import com.skaly.fashion_backend.ai.tryon.domain.port.StoragePort;
import com.skaly.fashion_backend.ai.tryon.domain.port.TryOnJob;
import com.skaly.fashion_backend.ai.tryon.domain.port.TryOnJobRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TryOnImageCleanupService {

    private final TryOnJobRepository tryOnJobRepository;
    private final StoragePort storagePort;

    // Chạy mỗi phút để xóa ảnh gốc của các job đã hoàn thành hoặc hết hạn (> 5 phút)
    @Scheduled(fixedRate = 60000)
    public void cleanupOriginalImages() {
        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(5);
        List<TryOnJob> expiredJobs = tryOnJobRepository
                .findAllByCreatedAtBeforeAndUserImageUrlIsNotNull(expirationTime);

        for (TryOnJob job : expiredJobs) {
            String imageUrl = job.getUserImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                try {
                    log.info("Deleting original image for job {}: {}", job.getId(), imageUrl);
                    storagePort.deleteFile(imageUrl);
                    job.setUserImageUrl(null);
                    tryOnJobRepository.save(job);
                } catch (Exception e) {
                    log.error("Failed to delete image for job {}: {}", job.getId(), e.getMessage());
                }
            }
        }
    }
}