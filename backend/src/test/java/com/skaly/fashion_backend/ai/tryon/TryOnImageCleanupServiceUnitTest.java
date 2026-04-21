package com.skaly.fashion_backend.ai.tryon;

import com.skaly.fashion_backend.ai.tryon.application.TryOnImageCleanupService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TryOnImageCleanupServiceUnitTest {

    @Mock
    private TryOnJobRepository tryOnJobRepository;

    @InjectMocks
    private TryOnImageCleanupService cleanupService;

    @Test
    void shouldDeleteOriginalImageForExpiredJobs() {
        // Given
        TryOnJob expiredJob = new TryOnJob();
        expiredJob.setId(UUID.randomUUID());
        expiredJob.setUserImageUrl("http://example.com/sensitive.jpg");

        when(tryOnJobRepository.findAllByCreatedAtBeforeAndUserImageUrlIsNotNull(any(LocalDateTime.class)))
                .thenReturn(List.of(expiredJob));

        // When
        cleanupService.cleanupOriginalImages();

        // Then
        verify(tryOnJobRepository, times(1)).save(argThat(job -> job.getUserImageUrl() == null));
    }
}
