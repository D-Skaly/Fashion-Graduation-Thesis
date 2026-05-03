# ADR-001 - Adopt Java 21 + Spring Boot 3

## Status

**Accepted**

## Context

The project needs to select a stable Java version and Spring Boot version that provides:

- **Long-Term Support (LTS)** for production stability
- **Virtual Threads support** for AI I/O operations
- **Compatibility** with Spring AI 1.0 and Spring Modulith
- **Mature ecosystem** with good library support

## Decision

Use the following technology stack:

- **Java 21** (LTS version)
- **Spring Boot 3.3.x**
- **Spring AI 1.0.x**
- **Spring Modulith 1.2.x**

## Rationale

### Why Java 21?

1. **LTS (Long-Term Support)** - Stable, well-supported, production-ready
2. **Virtual Threads** - Available and stable in Java 21, perfect for AI I/O operations
3. **Record classes & Pattern Matching** - Modern Java features for cleaner code
4. **Excellent ecosystem support** - Most libraries fully support Java 21

### Why Spring Boot 3.3.x?

1. **Java 21 native support** - Full integration with Virtual Threads
2. **Spring AI 1.0 compatibility** - Stable AI integration
3. **Spring Modulith support** - Compatible with modular monolith architecture
4. **Proven stability** - Well-tested in production environments

### Alternatives Considered

#### Java 25 + Spring Boot 4 (Future)
- **Pros:** Newer features, future-looking
- **Cons:** Not yet released as LTS, potential stability issues, limited library support

#### Java 17 + Spring Boot 3.0.x
- **Pros:** Earlier LTS version
- **Cons:** Missing some Java 21 features like mature Virtual Threads support

#### Java 11 + Spring Boot 2.7.x
- **Pros:** Very mature, wide adoption
- **Cons:** Missing modern features, no Virtual Threads, approaching end of support

## Consequences

### Positive

- Stable, production-ready technology stack
- Full Virtual Threads support for AI workloads
- Wide library compatibility
- Long-term support and security updates
- Easier hiring (more developers familiar with Java 21)
- Future-proof for at least 5+ years (Java 21 LTS timeline)

### Negative

- Missing some cutting-edge features from newer Java versions (e.g., Java 22+)
- May need future upgrade planning (but not urgent, Java 21 LTS supported until ~2029)

### Neutral

- Team may need training on Java 21 features (Virtual Threads, Pattern Matching, Records)
- Build tools and CI/CD need Java 21 compatibility

## Implementation Notes

- Enable Virtual Threads in `application.properties`:
  ```properties
  spring.threads.virtual.enabled=true
  ```
- Use structured concurrency with `StructuredTaskScope` for parallel operations
- Replace `ThreadLocal` with `ScopedValue` for context propagation
- See [`../concurrency.md`](../concurrency.md) for patterns

## Related Documents

- [Backend Setup](../backend.md) - Configuration examples
- [Concurrency Guide](../concurrency.md) - Java 21 Virtual Threads patterns
- [AI Integration](../ai-integration.md) - Spring AI 1.0 usage with Java 21
- [Architecture Guide](../architecture.md) - System design overview

## Notes

- This stack is targeted for MVP and production release
- Java 21 LTS support extends to at least 2029
- Plan for potential upgrade to Java 25 LTS (around 2029-2030) if needed
- Spring Boot 3.3.x will be supported until at least 2026-2027
