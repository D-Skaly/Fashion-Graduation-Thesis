# AI Integration Guide (Spring AI 1.0)

Comprehensive guide for integrating AI features using Spring AI 1.0 in the Fashion E-Commerce System.

---

## Overview

The system uses **Spring AI 1.0** for all AI capabilities:
- **RAG (Retrieval-Augmented Generation)** for product recommendations
- **Chat Assistants** for fashion styling advice
- **Virtual Try-On** integration (via AI Service)
- **Embeddings** for semantic search

---

## Core Principle: ChatClient Pattern (MANDATORY)

### ❌ DON'T: Use ChatModel Directly

```java
// WRONG - Don't do this
@Autowired
private ChatModel chatModel;

public String askQuestion(String prompt) {
    return chatModel.call(prompt);  // Direct call - AVOID
}
```

### ✅ DO: Use ChatClient with Advisors

```java
// CORRECT - Use ChatClient with Advisors
@Configuration
public class AiConfig {
    
    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder,
                                  ChatMemory chatMemory,
                                  VectorStore vectorStore) {
        return chatClientBuilder
            .defaultAdvisors(
                // Maintain conversation context
                new MessageChatMemoryAdvisor(chatMemory),
                // RAG - retrieve relevant documents
                new RetrievalAugmentationAdvisor(
                    vectorStore, 
                    SearchRequest.query("").topK(5)
                )
            )
            .build();
    }
}
```

Usage in service:
```java
@Service
public class FashionStylistService {
    
    private final ChatClient chatClient;
    
    public Flux<String> askStylist(String userQuery, String sessionId) {
        return chatClient.prompt()
            .user(userQuery)
            .advisors(a -> a.param("chat_memory_conversation_id", sessionId))
            .stream()
            .content();
    }
}
```

---

## RAG (Retrieval-Augmented Generation)

### Modular RAG with RetrievalAugmentationAdvisor

The system uses **Modular RAG** pattern with Spring AI 1.0:

```java
@Configuration
public class RagConfig {
    
    @Bean
    public RetrievalAugmentationAdvisor ragAdvisor(VectorStore vectorStore) {
        return RetrievalAugmentationAdvisor.builder()
            .documentRetriever(vectorStore::similaritySearch)
            .queryTransformer(query -> query + " for fashion styling")
            .build();
    }
}
```

### Multiple Retrievers (Plug & Play)

You can plug multiple retrievers if needed:

```java
// Combine multiple data sources
List<DocumentRetriever> retrievers = List.of(
    productVectorStore::similaritySearch,
    styleGuideVectorStore::similaritySearch,
    trendVectorStore::similaritySearch
);

// Use in advisor
RetrievalAugmentationAdvisor.builder()
    .documentRetriever(query -> 
        retrievers.stream()
            .flatMap(retriever -> retriever.retrieve(query).stream())
            .limit(10)
            .toList()
    )
    .build();
```

---

## MCP (Model Context Protocol)

### Defining Tools with @Tool

Tools MUST be defined using `@Tool` annotation in Spring AI core:

```java
@Service
public class FashionTools {
    
    @Tool(name = "search_products", description = "Search for fashion products")
    public List<Product> searchProducts(
            @ToolParameter(description = "Product category") String category,
            @ToolParameter(description = "Maximum number of results") int limit) {
        return productService.searchByCategory(category, limit);
    }
    
    @Tool(name = "get_size_chart", description = "Get size chart for a product")
    public SizeChart getSizeChart(
            @ToolParameter(description = "Product ID") String productId) {
        return productService.getSizeChart(productId);
    }
}
```

### Using MCP Client

For external tool integrations, use `spring-ai-starter-mcp-client`:

```yaml
# application.yml
spring:
  ai:
    mcp:
      client:
        enabled: true
        servers:
          - name: fashion-tools
            url: http://localhost:8081/mcp
```

---

## AI Design Rules (MANDATORY)

### Rule 1: AI Logic in Application Layer
```
module/ai/
├── domain/           → NO AI code here (pure business logic)
├── application/      ✅ AI use cases, ports, services HERE
├── infrastructure/  → AI adapters (implementations)
└── interfaces/       → Controllers (HTTP only, NO AI logic)
```

**Example:**
```java
// application/service/FashionRecommendationService.java
@Service
public class FashionRecommendationService {
    
    private final ChatClient chatClient;
    private final ProductRepository productRepo;  // Port, not JPA directly
    
    public Flux<String> recommend(String query, String userId) {
        // AI logic in application layer - CORRECT
        return chatClient.prompt()
            .user(buildPrompt(query, userId))
            .stream()
            .content();
    }
}
```

### Rule 2: NEVER Put AI Logic in Controllers
```java
// WRONG - Don't do this
@RestController
public class AIController {
    
    @Autowired
    private ChatModel chatModel;  // Direct injection - WRONG
    
    @PostMapping("/chat")
    public String chat(@RequestBody String message) {
        return chatModel.call(message);  // AI logic in controller - WRONG
    }
}
```

### Rule 3: Always Use Abstraction Layer
```java
// Define port in application layer
public interface AIModelPort {
    Flux<String> generate(Prompt prompt);
    List<Document> embed(String text);
}

// Implement in infrastructure layer
@Repository
public class SpringAIModelAdapter implements AIModelPort {
    
    private final ChatClient chatClient;
    private final EmbeddingClient embeddingClient;
    
    @Override
    public Flux<String> generate(Prompt prompt) {
        return chatClient.prompt().user(prompt.text()).stream().content();
    }
}
```

---

## Vector Store Integration (pgvector)

### Configuration
```yaml
# application.yml
spring:
  ai:
    vectorstore:
      pgvector:
        initialize-schema: true
        table-name: vector_store
        dimension: 768  # Match your embedding model
```

### Storing Embeddings
```java
@Service
public class ProductEmbeddingService {
    
    private final VectorStore vectorStore;
    private final EmbeddingClient embeddingClient;
    
    public void indexProduct(Product product) {
        // Create document with metadata
        Document doc = new Document(
            product.getDescription(),
            Map.of(
                "productId", product.getId(),
                "name", product.getName(),
                "category", product.getCategory(),
                "price", product.getPrice().toString()
            )
        );
        
        // Store in vector database
        vectorStore.add(List.of(doc));
    }
}
```

### Searching
```java
public List<Product> searchSimilarProducts(String query, int topK) {
    // Perform similarity search
    List<Document> docs = vectorStore.similaritySearch(
        SearchRequest.query(query).topK(topK)
    );
    
    // Convert back to products
    return docs.stream()
        .map(doc -> productRepo.findById(doc.getMetadata().get("productId")))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .toList();
}
```

---

## Streaming with SSE (Server-Sent Events)

### Controller Implementation
```java
@RestController
@RequestMapping("/api/v1/ai")
public class AIController {
    
    private final FashionStylistService stylistService;
    
    @GetMapping(value = "/chat/{sessionId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(
            @PathVariable String sessionId,
            @RequestParam String message) {
        
        return stylistService.askStylist(message, sessionId)
            .map(token -> "data: " + token + "\n\n");
    }
}
```

### Frontend Consumption (Next.js)
```typescript
// Using EventSource or fetch with ReadableStream
const eventSource = new EventSource(`/api/v1/ai/chat/${sessionId}/stream?message=${query}`);

eventSource.onmessage = (event) => {
    setResponse(prev => prev + event.data);
};
```

---

## AI Module Structure Example

```
ai/
├── domain/
│   ├── model/
│   │   ├── StylingAdvice.java
│   │   └── ProductRecommendation.java
│   └── repository/
│       └── AIConversationRepository.java (port)
│
├── application/
│   ├── port/
│   │   ├── AIModelPort.java
│   │   └── VectorStorePort.java
│   ├── service/
│   │   ├── FashionStylistService.java    ✅ AI logic here
│   │   └── ProductSearchService.java
│   └── dto/
│       ├── ChatRequest.java
│       └── ChatResponse.java
│
├── infrastructure/
│   ├── ai/
│   │   ├── SpringAIModelAdapter.java    ✅ AI implementation
│   │   └── RagConfiguration.java
│   └── persistence/
│       └── PgVectorStoreAdapter.java
│
└── interfaces/
    └── rest/
        └── AIController.java            ✅ HTTP only, no AI logic
```

---

## Testing AI Features

### Unit Tests (No Spring Context)
```java
@ExtendWith(MockitoExtension.class)
class FashionStylistServiceUnitTest {
    
    @Mock
    private AIModelPort aiModelPort;
    
    @Test
    void shouldGenerateStylingAdvice() {
        // Given
        when(aiModelPort.generate(any())).thenReturn(Flux.just("Style advice"));
        
        // When & Then
        // Test business logic without Spring context
    }
}
```

### Integration Tests (With Spring Context)
```java
@SpringBootTest
@TestPropertySource(properties = {"ai.enabled=false"})  // Disable real AI for tests
class FashionStylistIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void shouldCallChatEndpoint() {
        // Test with mocked AI responses
    }
}
```

---

## Configuration Properties

```yaml
# application.yml
ai:
  enabled: true
  
spring:
  ai:
    # Gemini Configuration
    gemini:
      api-key: ${GEMINI_API_KEY}
      model: gemini-pro
    
    # OpenAI Configuration (optional)
    openai:
      api-key: ${OPENAI_API_KEY}
      model: gpt-4
    
    # Vector Store
    vectorstore:
      pgvector:
        initialize-schema: true
        table-name: vector_store
        dimension: 768
    
    # Chat Client
    chat:
      client:
        enabled: true
```

---

## Error Handling

```java
@Service
public class AIService {
    
    private final ChatClient chatClient;
    
    public Mono<String> safeAsk(String prompt) {
        return Mono.from(chatClient.prompt()
                .user(prompt)
                .stream()
                .content())
            .timeout(Duration.ofSeconds(30))
            .onErrorResume(e -> {
                log.error("AI call failed", e);
                return Mono.just("Sorry, I'm having trouble right now. Please try again.");
            });
    }
}
```

---

## Best Practices

1. **Always use ChatClient** - Never call ChatModel directly
2. **Stream responses** - Use SSE for better UX
3. **Implement timeouts** - AI calls can hang
4. **Log carefully** - Don't log sensitive user data
5. **Use virtual threads** - For all AI I/O operations
6. **Cache when possible** - Reduce API calls
7. **Handle errors gracefully** - Provide fallback responses

---

## Related Documentation

- [Architecture Guide](architecture.md) - System design and module structure
- [Concurrency Guide](concurrency.md) - Virtual threads for AI operations
- [Agent Rules](agent-rules.md) - Coding standards
- [Prompt Patterns](prompt-patterns.md) - Standardized AI prompts
- [Data Privacy](data-privacy.md) - Privacy considerations for AI
