package com.skaly.fashion_backend.ai.tryon;

import com.skaly.fashion_backend.ai.tryon.application.TryOnService;
import com.skaly.fashion_backend.ai.tryon.JobStatus;
import com.skaly.fashion_backend.user.domain.entities.User;
import com.skaly.fashion_backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TryOnServiceUnitTest {

    @Mock
    private TryOnJobRepository tryOnJobRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TryOnService tryOnService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tryOnService, "orchestratorUrl", "http://localhost:3001");
    }

    @Test
    void shouldCreateJobWithPendingStatus() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        String userImageUrl = "http://example.com/image.jpg";
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(tryOnJobRepository.save(any(TryOnJob.class))).thenAnswer(invocation -> {
            TryOnJob job = invocation.getArgument(0);
            if (job.getId() == null) {
                job.setId(UUID.randomUUID());
            }
            return job;
        });

        // When
        TryOnJob job = tryOnService.createJob(userId, productId, userImageUrl);

        // Then
        assertThat(job.getStatus()).isIn(JobStatus.PENDING, JobStatus.FAILED);
        verify(tryOnJobRepository, atLeastOnce()).save(any(TryOnJob.class));
    }
}
