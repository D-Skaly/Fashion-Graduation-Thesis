# Documentation Hub - Fashion E-Commerce System

> **For AI Agents:** Also read [`.junie/AGENTS.md`](.junie/AGENTS.md) for mandatory coding standards.

## Quick Navigation

| Document | Description | Audience |
|----------|-------------|----------|
| [Architecture](architecture.md) | System design, module structure, core principles | Developers, Architects |
| [Backend Setup](backend.md) | Configuration, prerequisites, responsibilities | Developers |
| [AI Integration](ai-integration.md) | Spring AI patterns, RAG, MCP, design rules | Developers, AI Engineers |
| [Concurrency](concurrency.md) | Java 21 virtual threads, structured concurrency | Developers |
| [Data Privacy](data-privacy.md) | Privacy rules, streaming, evaluation, observability | Developers, Security |
| [Agent Rules](agent-rules.md) | Mandatory coding standards for AI agents | AI Agents, Developers |
| [Do's & Don'ts](do-dont.md) | Engineering guidelines and best practices | All |
| [Prompt Patterns](prompt-patterns.md) | AI prompt engineering patterns | AI Engineers |
| [ADR Index](adr/README.md) | Architecture Decision Records | Architects, Developers |

## Project Overview

**Fashion E-Commerce System** is a graduation thesis project featuring:
- **Modular Monolith** architecture with Spring Modulith
- **AI-First** design with RAG, assistants, and virtual try-on
- **Modern Stack**: Java 21, Next.js, NestJS, FastAPI

### Tech Stack Summary

```
Backend:     Java 21 + Spring Boot 3.3.x + Spring Modulith + Spring AI 1.0
Frontend:    Next.js (App Router) + TypeScript + Tailwind CSS + Shadcn UI
AI Layer:    NestJS (Orchestrator) + FastAPI (Service)
Database:    PostgreSQL 15+ with pgvector + Redis 7
Infrastructure: Docker, Nginx, MinIO
```

## Core Principles (MANDATORY)

These principles are enforced across all documentation and code:

1. **RULE-1**: Business logic MUST be framework-independent
2. **RULE-2**: NO cross-module repository access
3. **RULE-3**: External systems MUST go through ports/adapters
4. **RULE-4**: Use Virtual Threads for AI I/O operations
5. **RULE-5**: Privacy-first (no raw image storage)

## Module Structure (Clean Architecture)

Every module MUST follow this structure:

```
domain/         → Pure business logic (NO Spring annotations)
application/    → Use cases & ports (business workflow)
infrastructure/ → Adapters (JPA, VectorStore, external systems)
interfaces/     → REST/SSE controllers (HTTP concerns only)
```

## Documentation by Use Case

### I'm a Developer - Where to Start?

1. **New to project?** → Read [../README.md](../README.md) for setup instructions
2. **Understanding architecture?** → Read [architecture.md](architecture.md)
3. **Setting up backend?** → Read [backend.md](backend.md)
4. **Working with AI features?** → Read [ai-integration.md](ai-integration.md)
5. **Handling concurrency?** → Read [concurrency.md](concurrency.md)

### I'm an AI Agent - Coding Standards

1. **MANDATORY READ**: [../.junie/AGENTS.md](../.junie/AGENTS.md) - Complete coding guide
2. **Rules Check**: [agent-rules.md](agent-rules.md) - Module boundaries, testing, privacy
3. **Best Practices**: [do-dont.md](do-dont.md) - Engineering guidelines
4. **Prompt Patterns**: [prompt-patterns.md](prompt-patterns.md) - AI integration patterns

### I'm Working on Specific Features

| Feature Area | Read These Docs |
|--------------|-----------------|
| AI Chat/Assistant | [ai-integration.md](ai-integration.md), [prompt-patterns.md](prompt-patterns.md) |
| Virtual Try-On | [ai-integration.md](ai-integration.md), [data-privacy.md](data-privacy.md) |
| Database/API | [architecture.md](architecture.md), [backend.md](backend.md) |
| Performance | [concurrency.md](concurrency.md), [architecture.md](architecture.md) |
| Security/Privacy | [data-privacy.md](data-privacy.md), [agent-rules.md](agent-rules.md) |

## Architecture Decision Records (ADRs)

Located in [`adr/`](adr/README.md), these documents record significant architectural decisions:

- [ADR-001](adr/ADR-001-java25-upgrade.md): Java 25 Upgrade (Template)
- [ADR Template](adr/ADR-TEMPLATE.md): Use this template for new ADRs

## Quick Reference

### Virtual Threads (Java 21)
```java
// Enabled in application.properties
spring.threads.virtual.enabled=true

// Use for ALL I/O operations (LLM calls, DB queries, external APIs)
```

### Spring AI Pattern
```java
// DO: Use ChatClient with Advisors
chatClientBuilder
  .defaultAdvisors(
    new MessageChatMemoryAdvisor(chatMemory),
    new RetrievalAugmentationAdvisor(retriever)
  )
  .build();

// DON'T: Call ChatModel directly
```

### Module Boundaries
```java
// DO: Use ports in application layer
interface AIModelPort {
    AIResponse generate(Prompt prompt);
}

// DON'T: Access repositories across modules
```

## Related Resources

- **Main README**: [../README.md](../README.md) - Project setup and overview
- **Deployment Guide**: [../DEPLOY.md](../DEPLOY.md) - Production deployment
- **Agent Guide**: [../.junie/AGENTS.md](../.junie/AGENTS.md) - Mandatory for AI agents
- **Frontend README**: [../frontend/README.md](../frontend/README.md)
- **AI Service README**: [../ai-service/README.md](../ai-service/README.md)

## Contributing to Documentation

When adding new documentation:
1. Place in `docs/` directory
2. Use clear, descriptive filenames (kebab-case)
3. Add entry to this README's navigation table
4. Follow existing markdown structure
5. Include code examples where relevant
6. Mark TODOs clearly if documentation is incomplete

## Status

- ✅ Core architecture docs complete
- ✅ AI integration patterns documented
- ✅ Agent rules established
- 🔄 Frontend documentation in progress
- 🔄 API documentation pending
- 🔄 Testing guides being expanded
