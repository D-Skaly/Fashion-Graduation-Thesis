# Backend Setup

## Prerequisites

* Java 21 (LTS)
* Spring Boot 3.3.x
* Docker + Docker Compose
* PostgreSQL (pgvector)
* Redis

---

## application.properties

```properties
spring.threads.virtual.enabled=true

spring.ai.vectorstore.pgvector.initialize-schema=true
spring.ai.vectorstore.pgvector.table-name=vector_store
```

---

## Backend Responsibilities

* Domain execution
* API exposure (REST + SSE)
* AI orchestration entry point
* Integration with vector DB