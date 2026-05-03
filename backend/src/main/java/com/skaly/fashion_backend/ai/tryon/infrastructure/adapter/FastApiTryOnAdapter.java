package com.skaly.fashion_backend.ai.tryon.infrastructure.adapter;

import com.skaly.fashion_backend.ai.domain.port.ProductInfoPort;
import com.skaly.fashion_backend.ai.tryon.domain.port.TryOnPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Slf4j
@Component
public class FastApiTryOnAdapter implements TryOnPort {

    private final RestTemplate restTemplate;
    private final com.skaly.fashion_backend.ai.tryon.application.TryOnService tryOnService;
    private final com.skaly.fashion_backend.ai.tryon.application.TryOnNotificationService notificationService;
    private final ProductInfoPort productInfoPort;

    @Autowired
    public FastApiTryOnAdapter(
            RestTemplate restTemplate,
            @Lazy com.skaly.fashion_backend.ai.tryon.application.TryOnService tryOnService,
            com.skaly.fashion_backend.ai.tryon.application.TryOnNotificationService notificationService,
            ProductInfoPort productInfoPort) {
        this.restTemplate = restTemplate;
        this.tryOnService = tryOnService;
        this.notificationService = notificationService;
        this.productInfoPort = productInfoPort;
    }

    @Value("${application.ai.tryon.fastapi.url:http://localhost:8001}")
    private String fastApiUrl;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Override
    @Async
    public void requestTryOn(UUID jobId, UUID userId, UUID productId, String userImageUrl) {
        log.info("Sending Try-On request to FastAPI for Job: {}", jobId);
        
        if ("dev".equals(activeProfile) || activeProfile.contains("dev")) {
            simulateAiProcessing(jobId, userId, productId);
            return;
        }

        try {
            String url = fastApiUrl + "/api/v1/try-on";
            Map<String, String> payload = Map.of(
                    "job_id", jobId.toString(),
                    "user_id", userId.toString(),
                    "product_id", productId.toString(),
                    "image_url", userImageUrl != null ? userImageUrl : ""
            );
            
            // Real HTTP call to FastAPI service
            restTemplate.postForEntity(url, payload, Map.class);
            
            log.info("Try-On request successfully sent to FastAPI for Job: {}", jobId);
        } catch (Exception e) {
            log.error("Error calling FastAPI for Try-On Job: {}", jobId, e);
        }
    }

    private void simulateAiProcessing(UUID jobId, UUID userId, UUID productId) {
        log.info("[REAL-SIM] Simulating REAL AI processing for job: {} (ETA: 8s)", jobId);
        
        Thread.startVirtualThread(() -> {
            try {
                // Simulate deep learning latency
                Thread.sleep(Duration.ofSeconds(8));
                
                // Fetch product info via ProductInfoPort (decoupled from product module)
                var productInfo = productInfoPort.getProductInfo(productId);
                String productName = productInfo.name() != null ? productInfo.name() : "fashion item";
                
                // Use Pollinations.ai for DYNAMIC real-time image generation
                // Construct a professional fashion photography prompt
                String prompt = String.format("professional fashion photography, model wearing %s, high quality, studio lighting, lookbook style", 
                        productName.toLowerCase());
                String encodedPrompt = java.net.URLEncoder.encode(prompt, "UTF-8");
                String mockResultUrl = "https://pollinations.ai/p/" + encodedPrompt + "?width=1024&height=1024&seed=" + UUID.randomUUID().hashCode();
                
                var updatedJob = tryOnService.updateJobStatus(
                        jobId, 
                        com.skaly.fashion_backend.ai.tryon.domain.JobStatus.COMPLETED, 
                        mockResultUrl, 
                        null
                );
                
                // Push SSE update
                notificationService.notifyJobUpdate(userId, updatedJob);
                log.info("[REAL-SIM] Dynamic image generated for job: {}. URL: {}", jobId, mockResultUrl);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error("[REAL-SIM] Failed to generate dynamic image", e);
            }
        });
    }
}
