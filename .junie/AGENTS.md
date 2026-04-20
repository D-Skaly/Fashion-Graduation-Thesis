# Development Guide for Agents (Final Version)

## 0. Purpose of This Guide

This document is optimized for:

* AI agents (code generation, refactor, audit)
* Developers working in a modular monolith system

Primary goal:
Ensure consistent implementation aligned with **Clean Architecture + Spring Modulith + Business-first design**

---

# 1. Project Overview

## 1.1. Monorepo Structure

```
/backend        → Spring Boot (Java 21, Modulith)
/frontend       → Next.js 16 (TypeScript)
/orchestrator   → NestJS (AI orchestration)
/ai-service     → FastAPI (heavy AI processing)
```

---

## 1.2. Core Principles (MANDATORY)

```
RULE-1: Business logic MUST be independent from frameworks
RULE-2: DO NOT access repositories across modules
RULE-3: All external systems MUST go through ports/adapters
RULE-4: Prefer async/event-driven over direct calls
RULE-5: Privacy-first (no raw user image persistence)
```

---

# 2. Build & Configuration

## 2.1. Prerequisites

* Java 21
* Node.js 20+
* Docker & Docker Compose

---

## 2.2. Infrastructure Setup

```bash
docker-compose up -d
```

Services:

* PostgreSQL (with pgvector)
* Redis

---

## 2.3. Backend Setup

```bash
cd backend
cp .env.dev .env
./mvnw spring-boot:run
```

---

## 2.4. Frontend Setup

```bash
cd frontend
npm install
npm run dev
```

---

# 3. Architecture Rules (CRITICAL)

## 3.1. Modulith Structure (Outer Boundary)

Each module MUST be isolated:

```
com.project.product
com.project.order
com.project.tryon
```

---

## 3.2. Internal Module Structure (Clean Architecture)

Each module MUST follow:

```
module/
 ├── domain/
 ├── application/
 ├── infrastructure/
 └── interface/
```

---

## 3.3. Dependency Rules

Allowed:

* interface → application → domain
* infrastructure → domain (via adapters)

Forbidden:

* domain → infrastructure
* controller → repository (direct)
* module A → repository of module B

---

## 3.4. Ports & Adapters (REQUIRED)

```java
interface ProductRepositoryPort {
    Product findById(ProductId id);
}
```

```java
class JpaProductRepositoryAdapter implements ProductRepositoryPort {}
```

---

# 4. Testing

## 4.1. Testing Strategy

| Type             | Scope                | Framework      |
| ---------------- | -------------------- | -------------- |
| Unit Test        | Domain / Application | JUnit          |
| Integration Test | Spring Context       | SpringBootTest |

---

## 4.2. Golden Rule

```
Domain MUST be testable WITHOUT Spring
```

---

## 4.3. Running Tests

```bash
./mvnw test
./mvnw test -Dtest=ClassName
```

---

## 4.4. Test Naming Convention

* `*UnitTest.java`
* `*IntegrationTest.java`

---

## 4.5. Example

```java
class OrderPricingServiceUnitTest {
    private final OrderPricingService service = new OrderPricingService();

    @Test
    void shouldCalculateLineTotalCorrectly() {
        assertEquals(new BigDecimal("300.00"),
            service.calculateLineTotal(new BigDecimal("100"), 3));
    }
}
```

---

# 5. AI Integration Rules (CRITICAL)

## 5.1. DO NOT

```java
// WRONG
openAIClient.generate(...)
```

---

## 5.2. MUST USE

```java
interface AIModelPort {
    AIResponse generate(Prompt prompt);
}
```

---

# 6. Data & Privacy

## 6.1. Rules

```
- NEVER store raw user images permanently
- Use temporary storage (presigned URL)
- Auto-delete after processing
```

---

## 6.2. Try-On Pipeline

```
Client → Upload → Queue → AI Service → Result → WebSocket
```

---

# 7. Inter-Module Communication

## 7.1. Preferred Approach

```java
applicationEventPublisher.publishEvent(new ProductReservedEvent(...));
```

---

## 7.2. Avoid

* Direct service/repository calls across modules

---

# 8. Frontend Rules

* Default: React Server Components
* Use "use client" ONLY when necessary
* Follow API contract strictly

---

# 9. API Standard

```json
{
  "status": "success",
  "message": "OK",
  "data": {}
}
```

---

# 10. Troubleshooting

## 10.1. Flyway + pgvector

Error:

```
functions in index predicate must be marked IMMUTABLE
```

Fix:

* Check PostgreSQL version
* Ensure pgvector compatibility

---

## 10.2. Redis Issues

* Check container running
* Inspect ProductCacheService

---

# 11. DO & DON'T

## DO

* Write business logic in domain layer
* Use interfaces (ports)
* Write unit tests first
* Keep modules isolated
* Use events for communication

---

## DON'T

* Put logic in controller
* Use JPA entities as domain models
* Call repository across modules
* Hardcode AI providers
* Store user images permanently

---

# 12. Code Quality Checklist

* Domain has NO Spring annotations
* No cross-module repository access
* All external calls use ports
* Unit tests exist for business logic
* No sensitive data stored

---

# 13. Agent Execution Mode

```
STEP-1: Identify module
STEP-2: Work inside module boundary
STEP-3: Implement in domain → application → infra order
STEP-4: Add unit test
STEP-5: Validate architecture rules
```

---

# 14. Final Note

This system is designed to scale like microservices but run as a monolith.

Maintaining:

* Clean boundaries
* Business logic purity
* Replaceable AI components

is more important than writing less code.
