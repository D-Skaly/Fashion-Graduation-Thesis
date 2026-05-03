# Data & Privacy Guide

Comprehensive guide for handling data privacy, streaming, evaluation, and observability in the Fashion E-Commerce System.

---

## Privacy Rules (MANDATORY)

### Rule 1: No Raw Image Persistence
- **NEVER** store raw user images permanently on disk or database
- Process images in memory when possible
- Use MinIO or similar object storage with **automatic expiration**

### Rule 2: Minimize Stored User Data
- Only collect data that is absolutely necessary
- Implement data retention policies
- Regular cleanup of old/unused data
- Anonymize data when full identification isn't needed

### Rule 3: Use Anonymization When Possible
- Remove personally identifiable information (PII) from logs
- Use hashed identifiers instead of raw user IDs in analytics
- Anonymize datasets used for AI training

### Rule 4: Presigned URLs with Short Expiry
- Generate presigned URLs for image uploads/downloads
- Set expiry time to **less than 1 hour** (recommended: 15-30 minutes)
- Auto-delete images after processing is complete

---

## Image Handling (Virtual Try-On)

### Upload Flow
```
1. Frontend requests presigned URL → Backend (AI Module)
2. Backend generates presigned URL (expires in 30 min)
3. Frontend uploads image directly to MinIO using presigned URL
4. Backend triggers AI processing (try-on pipeline)
5. AI Service processes image → generates result
6. Result stored temporarily (presigned URL)
7. Frontend displays result
8. Auto-delete both original and result after 1 hour
```

### Implementation Example

**Generating Presigned URL:**
```java
@Service
public class ImageStorageService {
    
    private final MinioClient minioClient;
    private final String bucketName = "fashion-tryon";
    
    public String generateUploadUrl(String objectName, Duration expiry) {
        try {
            return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.PUT)
                    .bucket(bucketName)
                    .object(objectName)
                    .expiry((int) expiry.toSeconds())
                    .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate presigned URL", e);
        }
    }
    
    public void scheduleDeletion(String objectName, Duration delay) {
        // Schedule deletion after delay (use Spring's @Scheduled or message queue)
        log.info("Scheduled deletion for {} after {}", objectName, delay);
    }
}
```

**Auto-Deletion Configuration:**
```java
@Configuration
public class MinioConfig {
    
    @Bean
    public MinioClient minioClient(
            @Value("${minio.endpoint}") String endpoint,
            @Value("${minio.access-key}") String accessKey,
            @Value("${minio.secret-key}") String secretKey) {
        
        return MinioClient.builder()
            .endpoint(endpoint)
            .credentials(accessKey, secretKey)
            .build();
    }
    
    // Lifecycle rule for auto-expiration (configure on MinIO bucket)
    // Objects in "fashion-tryon" bucket expire after 1 hour
}
```

---

## Streaming (Server-Sent Events)

### Pattern: Flux<String> with SSE

**Backend Implementation:**
```java
@RestController
@RequestMapping("/api/v1/ai")
public class AIController {
    
    private final ChatService chatService;
    
    @GetMapping(value = "/chat/{sessionId}/stream", 
                produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(
            @PathVariable String sessionId,
            @RequestParam String message) {
        
        return chatService.askWithStreaming(message, sessionId)
            .map(token -> "data: " + token + "\n\n")
            .onErrorResume(e -> Flux.just(
                "data: [ERROR] " + e.getMessage() + "\n\n"
            ));
    }
}
```

**Frontend Consumption (Next.js):**
```typescript
export async function streamChat(sessionId: string, message: string) {
    const eventSource = new EventSource(
        `/api/v1/ai/chat/${sessionId}/stream?message=${encodeURIComponent(message)}`
    );
    
    return new ReadableStream({
        start(controller) {
            eventSource.onmessage = (event) => {
                controller.enqueue(event.data);
            };
            eventSource.onerror = () => {
                controller.close();
                eventSource.close();
            };
        }
    });
}
```

---

## AI Response Evaluation

### Mandatory Evaluators

Implement these evaluators for all AI features:

#### 1. FactCheckingEvaluator
```java
@Service
public class FashionFactChecker implements FactCheckingEvaluator {
    
    private final ProductRepository productRepo;
    
    @Override
    public EvaluationResult evaluate(String userQuery, String aiResponse) {
        // Extract product claims from AI response
        List<String> claimedProducts = extractProductClaims(aiResponse);
        
        for (String productName : claimedProducts) {
            Optional<Product> product = productRepo.findByName(productName);
            if (product.isEmpty()) {
                return EvaluationResult.failed(
                    "Product not found: " + productName
                );
            }
        }
        
        return EvaluationResult.passed();
    }
}
```

#### 2. RelevancyEvaluator
```java
@Service
public class FashionRelevancyEvaluator implements RelevancyEvaluator {
    
    @Override
    public double evaluateRelevancy(String query, String response) {
        // Simple keyword matching (enhance with embeddings)
        Set<String> queryKeywords = extractKeywords(query);
        Set<String> responseKeywords = extractKeywords(response);
        
        // Calculate Jaccard similarity
        Set<String> intersection = new HashSet<>(queryKeywords);
        intersection.retainAll(responseKeywords);
        
        Set<String> union = new HashSet<>(queryKeywords);
        union.addAll(responseKeywords);
        
        if (union.isEmpty()) return 0.0;
        return (double) intersection.size() / union.size();
    }
}
```

### Using Evaluators
```java
@Service
public class EvaluatedAIService {
    
    private final ChatClient chatClient;
    private final List<ResponseEvaluator> evaluators;
    
    public String askWithEvaluation(String query) {
        String response = chatClient.prompt()
            .user(query)
            .call()
            .content();
        
        // Run evaluators
        for (ResponseEvaluator evaluator : evaluators) {
            EvaluationResult result = evaluator.evaluate(query, response);
            if (!result.isPassed()) {
                log.warn("Evaluation failed: {}", result.getReason());
                // Optionally retry or return fallback
            }
        }
        
        return response;
    }
}
```

---

## Observability

### Logging Guidelines

**DO:**
```java
@Slf4j
@Service
public class AIService {
    
    public String ask(String prompt) {
        log.info("AI request received for session: {}", sessionId);  // OK
        
        // DON'T log the prompt directly if it contains PII
        log.debug("Prompt length: {}", prompt.length());  // Safer
        
        String response = callAI(prompt);
        
        log.info("AI response generated, length: {}", response.length());
        return response;
    }
}
```

**DON'T:**
```java
// NEVER do this
log.info("User prompt: {}", prompt);  // Contains PII!
log.info("AI response: {}", response);  // Might contain sensitive info!
```

### Structured Logging (JSON)
```yaml
# application.yml
logging:
  pattern:
    console: '{"timestamp":"%d{ISO8601}","level":"%level","message":"%msg","traceId":"%X{traceId}"}%n'
```

### Metrics (Planned)
```java
@Service
public class AIMetricsService {
    
    private final MeterRegistry meterRegistry;
    
    public void recordAICall(String model, Duration duration, boolean success) {
        Timer.Sample sample = Timer.start(meterRegistry);
        sample.stop(Timer.builder("ai.call.duration")
            .tag("model", model)
            .tag("success", String.valueOf(success))
            .register(meterRegistry));
    }
}
```

---

## Data Retention Policies

### User Data
| Data Type | Retention Period | Action After Expiry |
|-----------|-----------------|---------------------|
| Chat History | 90 days | Anonymize |
| Try-On Images | 1 hour | Auto-delete |
| User Profile | Until account deletion | Delete on request |
| Order History | 7 years (legal requirement) | Archive |

### Implementation
```java
@Component
public class DataRetentionScheduler {
    
    @Scheduled(cron = "0 0 2 * * ?")  // Every day at 2 AM
    public void cleanupExpiredData() {
        log.info("Starting data retention cleanup");
        
        // Delete old chat sessions
        chatRepository.deleteOlderThan(90, ChronoUnit.DAYS);
        
        // Delete expired try-on images (handled by MinIO lifecycle)
        // Archive old orders
        orderService.archiveOldOrders(7 * 365, ChronoUnit.DAYS);
    }
}
```

---

## Compliance Checklist

- [ ] GDPR compliance (right to be forgotten)
- [ ] Image data encrypted at rest
- [ ] HTTPS for all data transmission
- [ ] No PII in logs
- [ ] Data retention policies implemented
- [ ] User consent recorded for AI processing
- [ ] Presigned URLs with short expiry
- [ ] Auto-deletion of temporary files

---

## Security Best Practices

### Encryption
- **At Rest:** Use AES-256 for sensitive data storage
- **In Transit:** TLS 1.3 for all API communications
- **Database:** Enable PostgreSQL encryption extensions

### Access Control
```java
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
@GetMapping("/{id}")
public ProductDto getProduct(@PathVariable String id) {
    // Only authenticated users can access
}
```

### API Security
```yaml
# application.yml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://auth.fashion.com
```

---

## Incident Response

### Data Breach Protocol
1. **Detect** - Monitoring alerts trigger
2. **Contain** - Isolate affected systems
3. **Assess** - Determine scope of breach
4. **Notify** - Inform users and authorities (within 72 hours for GDPR)
5. **Remediate** - Fix vulnerabilities
6. **Review** - Post-mortem and improvements

---

## Related Documentation

- [Architecture Guide](architecture.md) - System design
- [AI Integration](ai-integration.md) - AI patterns and privacy
- [Agent Rules](agent-rules.md) - Coding standards (see Rule 5: Privacy-first)
- [Do's & Don'ts](do-dont.md) - Engineering guidelines
- [Concurrency Guide](concurrency.md) - Virtual threads for data processing
