# Architecture Decision Records (ADR)

## Purpose

This folder contains Architecture Decision Records (ADRs) that document important architectural decisions made during the Fashion E-Commerce System project.

## What is an ADR?

An ADR is a document that captures a significant architectural decision. Each ADR should:

- **Context:** Describe the situation and problem
- **Decision:** Explain what was decided
- **Rationale:** Justify why this decision was made
- **Consequences:** Note impacts (positive and negative)
- **Status:** Proposed | Accepted | Deprecated | Superseded

## Naming Convention

Use the format: `ADR-XXX-short-title.md`

Example: `ADR-001-java21-adoption.md`

## Template

See [`ADR-TEMPLATE.md`](ADR-TEMPLATE.md) for the standard format.

## List of ADRs

| ID | Title | Date | Status | Description |
|----|-------|------|--------|-------------|
| [ADR-001](ADR-001-java21-adoption.md) | Java 21 + Spring Boot 3 Adoption | 2026-04-29 | **Accepted** | Adopt Java 21 LTS and Spring Boot 3.3.x for long-term support and Virtual Threads |

## How to Create a New ADR

1. Copy `ADR-TEMPLATE.md` to a new file named `ADR-XXX-short-title.md`
2. Fill in all sections of the template
3. Update the table above with the new ADR
4. Submit for review

## ADR Statuses

- **Proposed:** Under discussion, not yet accepted
- **Accepted:** Decision made and implemented
- **Deprecated:** No longer recommended, but not removed
- **Superseded:** Replaced by a newer ADR (link to the new one)

## Example ADRs to Create

Consider documenting these decisions:

- **ADR-002:** Why Spring Modulith over Microservices?
- **ADR-003:** Choice of PostgreSQL with pgvector for vector storage
- **ADR-004:** Spring AI 1.0 adoption for AI integration
- **ADR-005:** Virtual Threads (Java 21) for concurrency model
- **ADR-006:** MinIO for object storage (instead of AWS S3)
- **ADR-007:** Next.js App Router for frontend framework
- **ADR-008:** Redis for caching strategy
- **ADR-009:** JWT + OAuth2 for authentication
- **ADR-010:** Flyway for database migrations

## Related Documentation

- [Architecture Guide](../architecture.md) - System design overview
- [Backend Setup](../backend.md) - Configuration details
- [Agent Rules](../agent-rules.md) - Coding standards
- [Main README](../README.md) - Project overview
