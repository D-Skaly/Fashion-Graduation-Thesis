# ADR-001 - Use Java 21 + Spring Boot 3

## Status

Accepted

## Context

The project needs to select a stable Java version and Spring Boot version that provides:
* Long-Term Support (LTS) for production stability
* Virtual Threads support for AI I/O operations
* Compatibility with Spring AI 1.0 and Spring Modulith
* Mature ecosystem with good library support

## Decision

Use the following technology stack:
* **Java 21** (LTS version)
* **Spring Boot 3.3.x**
* **Spring AI 1.0.x**
* **Spring Modulith 1.2.x**

## Rationale

### Why Java 21?
1. **LTS (Long-Term Support)** - Stable, well-supported, production-ready
2. **Virtual Threads** - Already available and stable in Java 21, perfect for AI I/O operations
3. **Record classes & Pattern Matching** - Modern Java features for cleaner code
4. **Excellent ecosystem support** - Most libraries fully support Java 21

### Why Spring Boot 3.3.x?
1. **Java 21 native support** - Full integration with Virtual Threads
2. **Spring AI 1.0 compatibility** - Stable AI integration
3. **Spring Modulith support** - Compatible with modular monolith architecture
4. **Proven stability** - Well-tested in production environments

### Alternatives Considered
1. **Java 25 + Spring Boot 4**
   - Pros: Newer features, future-looking
   - Cons: Not yet released as LTS, potential stability issues, limited library support

2. **Java 17 + Spring Boot 3.0.x**
   - Pros: Earlier LTS
   - Cons: Missing some Java 21 features like Virtual Threads maturity

## Consequences

### Positive
* Stable, production-ready technology stack
* Full Virtual Threads support for AI workloads
* Wide library compatibility
* Long-term support and security updates
* Easier hiring (more developers familiar with Java 21)

### Negative
* Missing some cutting-edge features from newer Java versions
* May need future upgrade planning (but not urgent)

## Notes

* See `backend.md` for configuration examples
* See `concurrency.md` for Java 21 Virtual Threads patterns
* See `ai-integration.md` for Spring AI 1.0 usage
* This stack is target for the MVP and production release
