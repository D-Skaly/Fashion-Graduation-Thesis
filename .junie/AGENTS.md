# Development Guide for Agents (Final Version - 2026 Update)

## 0. Purpose of This Guide

This document is optimized for:

- AI agents (code generation, refactor, audit)
- Developers working in a modular monolith system

**Primary goal:**
Ensure consistent implementation aligned with **Clean Architecture + Spring Modulith + Spring AI 1.0 + Java 21**

---

## 1. Project Overview

### 1.1. Monorepo Structure

```
/backend          → Spring Boot 3.3.x (Java 21, Spring Modulith, Spring AI 1.0)
/frontend         → Next.js (TypeScript)
/ai-orchestrator  → NestJS (AI orchestration, queues, gateway)
/ai-service       → FastAPI (heavy AI processing)
/docs             → Design notes, API docs
```

---

### 1.2. Core Principles (MANDATORY)

- **RULE-1:** Business logic MUST be independent from frameworks
- **RULE-2:** DO NOT access repositories across modules
- **RULE-3:** All external systems MUST go through ports/adapters
- **RULE-4:** Use Virtual Threads for all I/O-bound AI tasks
- **RULE-5:** Privacy-first (no raw user image persistence)

---

## 2. Build & Configuration

### 2.1. Prerequisites

- Java 21 (LTS)
- Spring Boot 3.3.x+
- Docker & Docker Compose (PostgreSQL with pgvector, Redis)

---

### 2.2. Backend Setup (application.properties)

```properties
# Enable Java 21 Virtual Threads
spring.threads.virtual.enabled=true

# Spring AI 1.0 Modular RAG configuration
spring.ai.vectorstore.pgvector.initialize-schema=true
spring.ai.vectorstore.pgvector.table-name=vector_store
```

---

## 3. Architecture Rules (CRITICAL)

### 3.1. Internal Module Structure (Clean Architecture)

Each module MUST follow this structure:

```
domain/         → Pure business logic (NO Spring annotations)
application/    → Use cases & ports
infrastructure/ → Adapters (JPA, VectorStore, MCP)
interfaces/     → REST / SSE controllers
```

---

### 3.2. Concurrency (Java 21)

- **Virtual Threads:**
  Always use for LLM calls and Vector Store operations to avoid thread blocking

- **Structured Concurrency:**
  Use `StructuredTaskScope` for parallel data fetching

- **Scoped Values:**
  Replace `ThreadLocal` for passing user context across virtual threads

---

## 4. AI Integration Rules (Spring AI 1.0)

### 4.1. ChatClient & Advisor Pattern

❌ DO NOT call `ChatModel` directly
✅ MUST use `ChatClient` with Advisors

```java
return chatClientBuilder
  .defaultAdvisors(
        new MessageChatMemoryAdvisor(chatMemory), // Context management
        new RetrievalAugmentationAdvisor(retriever) // Modular RAG
    )
  .build();
```

---

### 4.2. Model Context Protocol (MCP)

- Tools MUST be defined using `@Tool` in Spring AI core
- Use `spring-ai-starter-mcp-client` for external integrations

---

## 5. Data & Privacy

- **Streaming:**
  Use `stream()` API → return `Flux<String>` via Server-Sent Events (SSE)

- **Evaluation:**
  MUST implement:
  - `FactCheckingEvaluator`
  - `RelevancyEvaluator`

---

## 6. DO & DON'T

### ✅ DO

- Use `ReentrantLock` instead of `synchronized` (avoid thread pinning)
- Write unit tests for Domain logic FIRST
- Use `StructuredTaskScope.Joiner` for parallel AI task error handling

---

### ❌ DON'T

- Do NOT place AI logic inside Controllers
- Do NOT use JPA Entities in Domain layer
- Do NOT call OpenAI SDK directly if Spring AI abstraction exists
