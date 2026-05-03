# Concurrency Model (Java 21)

Guide to using Java 21 virtual threads and structured concurrency in the Fashion E-Commerce System.

---

## Overview

Java 21 introduces **Virtual Threads** (Project Loom), enabling high-throughput concurrent programming without the complexity of reactive programming.

**Key Benefits:**
- Massive scalability for I/O-bound tasks (AI calls, DB queries)
- Simple imperative coding style
- No callback hell or complex reactive chains

---

## Virtual Threads (MANDATORY for I/O)

### Enable Virtual Threads

**application.properties/application.yml:**
```properties
spring.threads.virtual.enabled=true
```

### When to Use Virtual Threads

✅ **DO use for:**
- LLM API calls (Gemini, OpenAI)
- Vector database operations
- External API calls
- Database queries (when using JDBC)
- Any I/O-bound operation

❌ **DON'T use for:**
- CPU-intensive computations (use platform threads)
- Tight loops without I/O

### Example: AI Service with Virtual Threads

```java
@Service
public class AIStylistService {
    
    private final ChatClient chatClient;
    
    // This method runs on a virtual thread automatically
    // thanks to @Transactional or @Async (configured with virtual threads)
    public Flux<String> askStylist(String prompt) {
        return chatClient.prompt()
            .user(prompt)
            .stream()
            .content();  // Each chunk processed on virtual thread
    }
}
```

### Spring Boot Auto-Configuration

With `spring.threads.virtual.enabled=true`:
- Tomcat uses virtual threads for request handling
- `@Async` methods use virtual threads
- JDBC operations can benefit from virtual threads

---

## Structured Concurrency

Use **StructuredTaskScope** for parallel operations that should succeed or fail together.

### Dependency (if not already included)
```xml
<dependency>
    <groupId>java.util.concurrent</groupId>
    <artifactId>structured-concurrency</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Pattern: Parallel Data Fetching

```java
public class ProductSearchService {
    
    public ProductSearchResult searchProducts(String query) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            
            // Launch parallel tasks
            Future<List<Product>> dbResults = scope.fork(() -> 
                productRepository.searchByText(query)
            );
            
            Future<List<Document>> vectorResults = scope.fork(() -> 
                vectorStore.similaritySearch(query)
            );
            
            Future<List<Trend>> trendResults = scope.fork(() ->
                trendService.getCurrentTrends()
            );
            
            // Wait for all tasks to complete (or fail)
            scope.join();
            scope.throwIfFailed();  // Propagate exceptions
            
            // Combine results
            return new ProductSearchResult(
                dbResults.resultNow(),
                vectorResults.resultNow(),
                trendResults.resultNow()
            );
        }
    }
}
```

### Pattern: Multi-Model AI Query

```java
public class MultiModelAIService {
    
    public String queryMultipleModels(String prompt) {
        try (var scope = new StructuredTaskScope.ShutdownOnSuccess<String>()) {
            
            // Try multiple AI models in parallel
            scope.fork(() -> geminiClient.ask(prompt));
            scope.fork(() -> openaiClient.ask(prompt));
            scope.fork(() -> claudeClient.ask(prompt));
            
            // Return first successful result
            scope.join();
            return scope.result();
        }
    }
}
```

### Using StructuredTaskScope.Joiner

```java
public class AggregationService {
    
    public AggregatedResult fetchAllData(String userId) {
        try (var scope = new StructuredTaskScope<>()) {
            
            StructuredTaskScope.Joiner<Void> joiner = 
                StructuredTaskScope.Joiner.create();
            
            // Fork multiple tasks
            joiner.fork(() -> fetchUserProfile(userId));
            joiner.fork(() -> fetchOrderHistory(userId));
            joiner.fork(() -> fetchRecommendations(userId));
            
            // Wait for all to complete
            scope.join();
            
            // Check for failures
            if (joiner.hasFailed()) {
                log.warn("Some tasks failed", joiner.exception());
            }
            
            return buildAggregatedResult();
        }
    }
}
```

---

## Scoped Values (Replace ThreadLocal)

**ThreadLocal** can cause issues with virtual threads (pinning). Use **ScopedValue** instead.

### ThreadLocal (Avoid with Virtual Threads)

❌ **DON'T:**
```java
private static final ThreadLocal<UserContext> userContext = new ThreadLocal<>();

public void process() {
    userContext.set(new UserContext(...));
    try {
        // do work
    } finally {
        userContext.remove();  // Easy to forget!
    }
}
```

### ScopedValue (Recommended)

✅ **DO:**
```java
public class UserContext {
    static final ScopedValue<UserContext> CONTEXT = ScopedValue.newInstance();
    
    public static void processRequest(UserContext ctx, Runnable action) {
        ScopedValue.where(CONTEXT, ctx)
                   .run(action);  // ctx is available within this scope
    }
}

// Usage
UserContext.processRequest(currentUser, () -> {
    // CONTEXT.get() works here
    UserContext ctx = UserContext.CONTEXT.get();
    // do work with ctx
});
```

### Example: Passing User Context in Virtual Threads

```java
@RestController
public class AIController {
    
    @GetMapping("/chat/stream")
    public Flux<String> chat(@RequestParam String message,
                              @AuthenticationPrincipal User user) {
        
        // Set user context for virtual thread
        UserContext ctx = new UserContext(user.getId(), user.getRoles());
        
        return UserContext.runWithContext(ctx, () -> {
            // This runs on a virtual thread with context available
            return aiService.ask(message);
        });
    }
}
```

---

## Best Practices

### 1. Avoid Blocking Operations
```java
// BAD: Blocking sleep
Thread.sleep(1000);

// GOOD: Virtual thread friendly
await(Duration.ofSeconds(1));
```

### 2. Design Async-First Flows
```java
// Return reactive types for streaming
public Flux<String> streamResponse(String prompt) {
    return chatClient.prompt()
        .user(prompt)
        .stream()
        .content();  // Returns Flux (reactive stream)
}
```

### 3. Combine with Streaming (SSE)
```java
@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<String> streamChat(@RequestParam String message) {
    return aiService.ask(message)
        .map(token -> "data: " + token + "\n\n");
}
```

### 4. Use Timeouts
```java
public Mono<String> safeAICall(String prompt) {
    return Mono.from(aiService.ask(prompt))
        .timeout(Duration.ofSeconds(30))
        .onErrorResume(e -> Mono.just("Request timed out"));
}
```

---

## Common Patterns

### Pattern: Parallel API Calls
```java
public class ProductService {
    
    public CompletableFuture<ProductDetails> getDetails(String productId) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            
            Future<Product> product = scope.fork(() -> 
                productRepo.findById(productId)
            );
            
            Future<List<Review>> reviews = scope.fork(() ->
                reviewService.getReviews(productId)
            );
            
            Future<StockInfo> stock = scope.fork(() ->
                inventoryService.checkStock(productId)
            );
            
            scope.join();
            scope.throwIfFailed();
            
            return new ProductDetails(
                product.resultNow(),
                reviews.resultNow(),
                stock.resultNow()
            );
        }
    }
}
```

### Pattern: Timeout with Fallback
```java
public String getWithTimeout(String prompt) {
    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
        
        Future<String> aiCall = scope.fork(() -> 
            aiClient.ask(prompt)
        );
        
        // Shutdown after timeout
        scope.fork(() -> {
            Thread.sleep(5000);
            scope.shutdown();  // Cancel the AI call
            return null;
        });
        
        scope.join();
        return aiCall.resultNow();
        
    } catch (Exception e) {
        return getCachedResponse(prompt);  // Fallback
    }
}
```

---

## Troubleshooting

### Issue: Thread Pinning
**Symptom:** Virtual threads not improving throughput

**Cause:** Using `synchronized` blocks or `ThreadLocal`

**Solution:**
```java
// Replace synchronized with ReentrantLock
private final ReentrantLock lock = new ReentrantLock();

public void criticalSection() {
    lock.lock();
    try {
        // do work
    } finally {
        lock.unlock();
    }
}
```

### Issue: Context Not Available
**Symptom:** `ScopedValue.get()` returns null

**Cause:** Not using `ScopedValue.where().run()`

**Solution:** Ensure proper scoping:
```java
ScopedValue.where(MY_VALUE, value)
           .run(() -> { /* use value here */ });
```

---

## Testing with Virtual Threads

```java
@SpringBootTest
class ConcurrencyTest {
    
    @Test
    void shouldHandleManyConcurrentRequests() {
        int numRequests = 10000;
        CountDownLatch latch = new CountDownLatch(numRequests);
        
        for (int i = 0; i < numRequests; i++) {
            // Each request runs on a virtual thread
            Thread.startVirtualThread(() -> {
                try {
                    // Simulate request
                    aiService.ask("test");
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(10, TimeUnit.SECONDS);
        // Verify all completed successfully
    }
}
```

---

## Performance Considerations

| Operation | Platform Threads | Virtual Threads |
|-----------|-----------------|-----------------|
| AI API Calls | ~1000 concurrent | ~1M concurrent |
| DB Queries (JDBC) | Limited by pool | Limited by DB |
| Context Switching | Expensive | Cheap |
| Memory per Thread | ~1MB | ~200 bytes |

---

## Related Documentation

- [Architecture Guide](architecture.md) - System design and module structure
- [AI Integration](ai-integration.md) - Using virtual threads for AI calls
- [Agent Rules](agent-rules.md) - Coding standards
- [Do's & Don'ts](do-dont.md) - Best practices
