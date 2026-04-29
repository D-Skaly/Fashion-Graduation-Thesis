# Architecture Guide

## Monorepo Structure

/backend          → Spring Boot 3 (Java 21)
/frontend         → Next.js
/ai-orchestrator  → NestJS
/ai-service       → FastAPI
/docs             → Documentation

---

## Core Principles (MANDATORY)

* RULE-1: Business logic MUST be framework-independent
* RULE-2: NO cross-module repository access
* RULE-3: External systems MUST go through ports/adapters
* RULE-4: Use Virtual Threads for AI I/O
* RULE-5: Privacy-first (no raw image storage)

---

## Module Structure (Clean Architecture)

domain/         → Pure logic
application/    → Use cases & ports
infrastructure/ → Adapters
interfaces/     → Controllers

---

## Design Philosophy

* Modular Monolith (Spring Modulith)
* Event-driven inside modules
* Strict boundary enforcement