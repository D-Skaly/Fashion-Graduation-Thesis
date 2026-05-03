# DO & DON'T - Engineering Guidelines

Quick reference for coding standards and best practices in the Fashion E-Commerce System.

---

## ✅ DO (Best Practices)

### Concurrency (Java 21)
- **DO** use `ReentrantLock` instead of `synchronized` (avoids thread pinning with virtual threads)
- **DO** write domain unit tests first (before implementation)
- **DO** use `StructuredTaskScope.Joiner` for parallel AI task execution
- **DO** use `ScopedValue` instead of `ThreadLocal` for context propagation
- **DO** design async-first flows for AI operations

### Architecture & Design
- **DO** keep modules isolated (no cross-module dependencies)
- **DO** use ports/adapters pattern for external systems
- **DO** put business logic in `domain/` layer (framework-independent)
- **DO** use `application/` layer for use cases and orchestration
- **DO** implement adapters in `infrastructure/` layer
- **DO** keep controllers thin (only HTTP concerns in `interfaces/`)

### AI Integration
- **DO** use `ChatClient` with Advisors (not `ChatModel` directly)
- **DO** define tools with `@Tool` annotation
- **DO** use `RetrievalAugmentationAdvisor` for RAG
- **DO** implement AI logic in `application/` layer
- **DO** stream AI responses via SSE (Server-Sent Events)
- **DO** use virtual threads for all AI I/O operations

### Code Quality
- **DO** prefer constructor injection over field injection
- **DO** write tests for business logic (domain layer)
- **DO** use meaningful variable and method names
- **DO** handle errors gracefully with fallbacks
- **DO** log at appropriate levels (INFO, WARN, ERROR)
- **DO** use Lombok `@Slf4j` for logging

### Data & Privacy
- **DO** minimize stored user data
- **DO** use presigned URLs with short expiry (< 1 hour)
- **DO** auto-delete images after processing
- **DO** use anonymization when possible
- **DO** implement `FactCheckingEvaluator` and `RelevancyEvaluator`

---

## ❌ DON'T (Anti-Patterns)

### Architecture Violations
- **DON'T** put AI logic in Controllers (`interfaces/` layer)
- **DON'T** use JPA Entities in Domain layer (keep domain pure)
- **DON'T** call OpenAI/Claude SDK directly (use Spring AI abstraction)
- **DON'T** access repositories across modules (violates module boundaries)
- **DON'T** break module boundaries (use events or ports for communication)

### Concurrency Mistakes
- **DON'T** use `ThreadLocal` with virtual threads (use `ScopedValue`)
- **DON'T** block virtual threads with synchronized blocks
- **DON'T** perform long-running operations without timeouts
- **DON'T** ignore error handling in parallel operations

### AI Integration Mistakes
- **DON'T** call `ChatModel` directly (always use `ChatClient`)
- **DON'T** put AI business logic in Controllers
- **DON'T** store raw user images permanently
- **DON'T** log sensitive user data or AI responses without care
- **DON'T** make AI calls without timeouts

### Code Smells
- **DON'T** write business logic in Controllers
- **DON'T** use field injection (`@Autowired` on fields)
- **DON'T** create "god classes" with too many responsibilities
- **DON'T** ignore exceptions (at least log them)
- **DON'T** use `System.out.println()` for logging

---

## Engineering Mindset

### Design Principles
- **Design for scale from day-1** - Even as a modular monolith, think about future microservices extraction
- **Optimize for AI-agent readability** - Code should be self-documenting and clear
- **Keep code deterministic & testable** - Avoid hidden side effects, make dependencies explicit
- **Privacy-first approach** - Always consider data privacy in design decisions

### Code Review Checklist
- [ ] Business logic is in `domain/` or `application/` layer
- [ ] No cross-module repository access
- [ ] External systems go through ports/adapters
- [ ] Virtual threads used for I/O operations
- [ ] No sensitive data in logs
- [ ] Unit tests written for business logic
- [ ] Error handling implemented
- [ ] Code follows naming conventions

---

## Quick Reference Card

| Concern | DO ✅ | DON'T ❌ |
|---------|-------|----------|
| **AI Calls** | `ChatClient` with Advisors | `ChatModel` directly |
| **Threading** | Virtual threads + `ReentrantLock` | `synchronized` + `ThreadLocal` |
| **Module Access** | Events/ports for cross-module | Direct repository access |
| **Layer Violations** | Logic in `domain/`/`application/` | Logic in `interfaces/` (Controllers) |
| **Dependency Injection** | Constructor injection | Field injection (`@Autowired`) |
| **Testing** | Unit tests for domain logic | No tests / only integration tests |
| **Data Privacy** | Presigned URLs, auto-delete | Store raw images permanently |

---

## Examples

### ✅ Good: Proper Layer Separation
```java
// domain/Product.java - Pure business logic
public class Product {
    private ProductId id;
    private String name;
    private Price price;
    
    public boolean isOnSale() {
        return price.hasDiscount();
    }
}

// application/ProductService.java - Use case
@Service
public class ProductService {
    public Product findProduct(ProductId id) {
        // orchestration logic
    }
}

// interfaces/ProductController.java - HTTP only
@RestController
public class ProductController {
    @GetMapping("/{id}")
    public ProductDto getProduct(@PathVariable String id) {
        // HTTP concerns only
    }
}
```

### ❌ Bad: Mixed Concerns
```java
@RestController
public class BadController {
    
    @Autowired  // DON'T: field injection
    private ProductRepository repo;  // DON'T: direct repository access
    
    @PostMapping("/")
    public String createProduct(@RequestBody ProductDto dto) {
        // DON'T: business logic in controller
        Product p = new Product(dto.getName());
        if (p.getName().length() < 3) {
            throw new IllegalArgumentException("Name too short");
        }
        repo.save(p);
        
        // DON'T: AI logic in controller
        String result = chatModel.call("Describe " + dto.getName());
        return result;
    }
}
```

---

## Related Documentation

- [Agent Rules](agent-rules.md) - Mandatory coding standards
- [Architecture Guide](architecture.md) - System design
- [AI Integration](ai-integration.md) - AI patterns
- [Concurrency Guide](concurrency.md) - Virtual threads
- [Data Privacy](data-privacy.md) - Privacy rules
