# Architecture Guide - Fashion E-Commerce System

## Overview

This document describes the system architecture of the Fashion E-Commerce platform, built using **Spring Modulith** for a modular monolith approach with clear boundaries and event-driven communication.

---

## Monorepo Structure

```text
/
├── backend/                → Spring Boot 3 (Java 21, Spring Modulith)
│   └── src/main/java/com/fashion/backend/
│       ├── product/        → Product module
│       ├── order/          → Order module
│       ├── customer/       → Customer module
│       ├── ai/             → AI module
│       └── shared/         → Shared kernel
├── frontend/               → Next.js (TypeScript, App Router)
├── ai-orchestrator/        → NestJS (AI workflow orchestration)
├── ai-service/             → FastAPI (Heavy AI processing)
└── docs/                   → Documentation
```

---

## Core Principles (MANDATORY)

These principles MUST be followed in all implementation work:

### RULE-1: Framework Independence
**Business logic MUST be framework-independent**
- Domain layer should have NO Spring annotations
- Use pure Java classes in `domain/` packages
- Framework dependencies only in `infrastructure/` and `interfaces/`

### RULE-2: Module Boundaries
**NO cross-module repository access**
- Each module owns its data
- Use events or ports for inter-module communication
- Never inject repositories from another module

### RULE-3: Port/Adapter Pattern
**External systems MUST go through ports/adapters**
- Define ports in `application/` layer
- Implement adapters in `infrastructure/` layer
- Supports easy testing and technology changes

### RULE-4: Virtual Threads
**Use Virtual Threads for AI I/O operations**
- Enable with `spring.threads.virtual.enabled=true`
- Use for LLM calls, vector DB operations, external API calls
- Avoids thread blocking in Java 21+

### RULE-5: Privacy First
**No raw image storage**
- Process and discard user images immediately
- Use presigned URLs with short expiry (< 1 hour)
- Auto-delete after processing
- Minimize personal data storage

---

## Module Structure (Clean Architecture)

Every module MUST follow this structure:

```
module-name/
├── domain/                 → Pure business logic
│   ├── model/             → Entities, Value Objects, Aggregates
│   ├── repository/        → Repository interfaces (ports)
│   ├── service/           → Domain services (pure logic)
│   └── event/             → Domain events
│
├── application/            → Use cases & ports
│   ├── port/              → Input/output ports (interfaces)
│   ├── service/           → Application services (use cases)
│   ├── event/             → Application event handlers
│   └── dto/               → Data Transfer Objects
│
├── infrastructure/         → Adapters (implements ports)
│   ├── persistence/       → JPA entities, repositories
│   ├── external/          → External service adapters
│   ├── messaging/         → Message broker adapters
│   └── ai/                → AI service adapters
│
└── interfaces/             → Controllers (HTTP concerns only)
    ├── rest/              → REST API controllers
    ├── sse/               → Server-Sent Events controllers
    └── dto/               → Request/Response DTOs
```

### Layer Responsibilities

| Layer | Responsibility | Dependencies |
|-------|---------------|--------------|
| **Domain** | Core business logic, rules, models | None (pure Java) |
| **Application** | Use cases, orchestration, ports | Domain |
| **Infrastructure** | Adapters, frameworks, external systems | Application, Domain |
| **Interfaces** | HTTP controllers, request/response handling | Application |

---

## System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                        Frontend (Next.js)                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐        │
│  │ Product Page │  │ Cart/Order   │  │ AI Features  │        │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘        │
└─────────┼──────────────────┼──────────────────┼────────────────┘
          │                  │                  │
          │ REST API         │ REST API         │ REST API + SSE
          │                  │                  │
┌─────────┼──────────────────┼──────────────────┼────────────────┐
│         ▼                  ▼                  ▼                │
│  ┌──────────────────────────────────────────────────────┐     │
│  │         Backend (Spring Boot + Modulith)             │     │
│  │                                                      │     │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐         │     │
│  │  │ Product  │  │  Order   │  │    AI    │         │     │
│  │  │ Module   │◄─┤ Module   │  │ Module   │         │     │
│  │  └────┬─────┘  └────┬─────┘  └────┬─────┘         │     │
│  │       │              │              │               │     │
│  │       └──────┬───────┴──────────────┘               │     │
│  │              ▼                                      │     │
│  │  ┌─────────────────────────────────────────┐        │     │
│  │  │         Shared Kernel                   │        │     │
│  │  │  (Common types, utils, config)          │        │     │
│  │  └─────────────────────────────────────────┘        │     │
│  └──────────────────────────────────────────────────────┘     │
│                      │                                        │
│         ┌──────────┬┴──────────┬──────────┐                  │
│         ▼          ▼           ▼          ▼                  │
│    ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐             │
│    │ PostgreSQL│ │ Redis  │ │ Vector │ │ MinIO  │             │
│    │ +pgvector│ │(Cache) │ │ Store  │ │(Images)│             │
│    └────────┘ └────────┘ └────────┘ └────────┘             │
└──────────────────────────────────────────────────────────────┘
                      │
                      │ AI Processing
                      ▼
┌──────────────────────────────────────────────────────────────┐
│              AI Layer (Separate Services)                    │
│                                                              │
│  ┌──────────────────┐      ┌──────────────────┐            │
│  │ AI Orchestrator  │──────▶│   AI Service     │            │
│  │   (NestJS)       │      │   (FastAPI)      │            │
│  │ - Workflow mgmt  │      │ - Try-on pipeline│            │
│  │ - Queue mgmt     │      │ - Model inference│            │
│  └──────────────────┘      └──────────────────┘            │
└──────────────────────────────────────────────────────────────┘
```

---

## Module Communication

### Synchronous Communication
- **Within module:** Direct method calls
- **Cross-module:** Through `application/` ports (interfaces)

### Asynchronous Communication (Events)
Using Spring Modulith's event publishing:

```java
// Publish domain event
@DomainEventPublisher
public class OrderPlacedEvent {
    // event data
}

// Listen to event in another module
@ApplicationModuleListener
public void handleOrderPlaced(OrderPlacedEvent event) {
    // handle event
}
```

---

## Design Philosophy

### Modular Monolith
- **Benefits:** Simple deployment, strong consistency, easy testing
- **Trade-offs:** Less scalability than microservices (acceptable for thesis)
- **When to extract:** If a module needs independent scaling

### Event-Driven Inside Modules
- Use domain events for side effects
- Decouple primary logic from secondary operations
- Enable async processing where appropriate

### Strict Boundary Enforcement
- Spring Modulith enforces module access rules
- Compile-time and runtime checks available
- Prevents accidental boundary violations

---

## Technology Decisions

### Why Spring Modulith?
- Strong module boundaries with enforcement
- Event-driven capabilities built-in
- Easier than microservices for graduation project
- Can migrate to microservices later if needed

### Why Virtual Threads (Java 21)?
- Massive throughput improvement for I/O-bound tasks
- Simpler concurrency model than reactive
- Perfect for AI API calls (many waiting requests)

### Why Spring AI 1.0?
- Unified abstraction for multiple AI providers
- RAG support with Modular RAG pattern
- ChatClient with Advisor pattern
- MCP (Model Context Protocol) support

---

## Data Flow Examples

### Product Search with AI
```
User Request → Frontend → Backend (Product Module)
                        ↓
              Application Service (search use case)
                        ↓
              Check Cache (Redis) → Return if exists
                        ↓ (not in cache)
              Query Vector Store (pgvector) for semantic search
                        ↓
              Return results + trigger async indexing
```

### AI Chat with RAG
```
User Message → Backend (AI Module)
              ↓
       ChatClient with Advisors:
       - MessageChatMemoryAdvisor (context)
       - RetrievalAugmentationAdvisor (RAG)
              ↓
       Query Vector Store → Retrieve relevant docs
              ↓
       Call LLM (Gemini/OpenAI) with context
              ↓
       Stream response via SSE (Flux<String>)
```

---

## Scalability Considerations

### Current (Modular Monolith)
- Single deployment unit
- Shared database (with schema separation)
- Suitable for initial launch and thesis

### Future Migration Path
- Extract AI module → AI Service (already done)
- Extract Order module → Order Service
- Extract Product module → Product Service
- Use Spring Cloud for service discovery

---

## Security Architecture

### Authentication
- OAuth2 + JWT tokens
- Spring Security configuration
- Token validation in `interfaces/` layer

### Authorization
- Role-based access control (RBAC)
- Method-level security with `@PreAuthorize`
- Module-specific authorization rules

### Data Privacy
- See [Data Privacy Guide](data-privacy.md)
- No raw image persistence
- Encryption at rest for sensitive data

---

## Monitoring & Observability

### Logging
- Structured logging (JSON format)
- Correlation IDs for request tracing
- Avoid logging sensitive data (see Data Privacy)

### Metrics (Planned)
- Spring Boot Actuator
- Custom business metrics
- AI model performance tracking

### Tracing (Planned)
- Distributed tracing with Micrometer
- Trace AI request flows across services

---

## Related Documentation

- [Backend Setup](backend.md) - Configuration and prerequisites
- [AI Integration](ai-integration.md) - Spring AI patterns
- [Concurrency](concurrency.md) - Virtual threads and structured concurrency
- [Data Privacy](data-privacy.md) - Privacy rules and implementation
- [Agent Rules](agent-rules.md) - Coding standards for AI agents
