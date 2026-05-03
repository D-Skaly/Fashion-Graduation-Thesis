# Agent Rules (AI Coding Standards)

## Purpose

This file provides mandatory guidelines for AI agents (Cursor, Copilot, etc.) when generating or refactoring code in this project.

---

## 1. Module Boundary Rules

* NEVER access repositories across modules
* Use `application/` layer to coordinate between modules via events or ports
* Each module must have: `domain/`, `application/`, `infrastructure/`, `interfaces/`

---

## 2. AI Integration Rules

* NEVER import OpenAI/Claude SDK directly in business code
* Always define `AIModelPort` in `domain/` or `application/`
* Implement adapters in `infrastructure/`

Example:
```java
// domain/port/AIModelPort.java
interface AIModelPort {
    AIResponse generate(Prompt prompt);
}
```

---

## 3. Testing Rules

* Domain tests: NO Spring context (`*UnitTest.java`)
* Integration tests: With Spring context (`*IntegrationTest.java`)
* Test naming: `*UnitTest.java` or `*IntegrationTest.java`

---

## 4. Concurrency Rules

* Use Virtual Threads for ALL I/O (Java 21+)
* Use `StructuredTaskScope` for parallel operations
* Replace `ThreadLocal` with `ScopedValue`

---

## 5. Privacy Rules

* NEVER store raw user images permanently
* Use Presigned URLs with short expiry (< 1 hour)
* Auto-delete images after processing

---

## 6. Code Style

* Use `ReentrantLock` instead of `synchronized`
* Prefer constructor injection over field injection
* Keep Controllers thin (only HTTP concerns)

---

## 7. Agent Workflow

When working on a task:

1. Identify the module (product, order, ai, etc.)
2. Work INSIDE module boundaries
3. Implement: domain → application → infrastructure
4. Add unit tests for business logic
5. Validate architecture rules before completing