# Backend Setup Guide

Complete guide for setting up and running the Spring Boot backend with Spring Modulith.

---

## Prerequisites

### Required Software
- **Java 21 (LTS)** - [Download](https://www.oracle.com/java/technologies/downloads/)
- **Maven 3.9+** (or use included Maven Wrapper `mvnw`)
- **Docker Desktop** - [Download](https://www.docker.com/products/docker-desktop/)
- **PostgreSQL 15+** (via Docker - see below)
- **Redis 7+** (via Docker - see below)

### Recommended Tools
- **IDE:** IntelliJ IDEA (Community/Ultimate) or VS Code with Java extensions
- **API Testing:** Postman, Insomnia, or cURL
- **Database Client:** DBeaver, pgAdmin, or psql CLI

---

## Quick Start (3 Steps)

```bash
# 1. Start infrastructure (PostgreSQL + Redis)
docker-compose up -d

# 2. Navigate to backend and run
cd backend
./mvnw spring-boot:run

# 3. Verify backend is running
curl http://localhost:8080/api/v1/actuator/health
```

Backend API available at: http://localhost:8080/api/v1

---

## Detailed Setup

### 1. Infrastructure Setup (Docker)

Start required services using Docker Compose:

```bash
docker-compose up -d
```

This starts:
- **PostgreSQL** (port 5432) with pgvector extension
- **Redis** (port 6379) for caching

Verify services are running:
```bash
docker-compose ps

# Check logs if needed
docker-compose logs postgres
docker-compose logs redis
```

### 2. Environment Configuration

Create environment file from template:

**Windows:**
```bash
cd backend
copy .env.dev .env
```

**Linux/Mac:**
```bash
cd backend
cp .env.dev .env
```

Edit `.env` file with your configuration:

```properties
# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/fashion_db_dev
DATABASE_USERNAME=fashion_user
DATABASE_PASSWORD=fashion_pass

# Spring Profile
SPRING_PROFILES_ACTIVE=dev

# JWT
JWT_SECRET_KEY=your-secret-key-here-change-in-production

# AI Configuration
AI_ENABLED=true
GEMINI_API_KEY=your-gemini-api-key-here
OPENAI_API_KEY=your-openai-key-here # Optional

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# MinIO (Object Storage)
MINIO_ENDPOINT=http://localhost:9000
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin
```

### 3. Run the Application

**Using Maven Wrapper (Recommended):**
```bash
cd backend
./mvnw spring-boot:run
```

**Using installed Maven:**
```bash
cd backend
mvn spring-boot:run
```

**With specific profile:**
```bash
./mvnw spring-boot:run -Dspring.profiles.active=dev
```

### 4. Verify Installation

Check if the application is running:

```bash
# Health check
curl http://localhost:8080/api/v1/actuator/health

# API info
curl http://localhost:8080/api/v1/actuator/info
```

Expected response:
```json
{
  "status": "UP"
}
```

---

## Application Configuration

### Key application.properties / application.yml

```properties
# Enable Java 21 Virtual Threads (MANDATORY)
spring.threads.virtual.enabled=true

# Spring AI - Vector Store (pgvector)
spring.ai.vectorstore.pgvector.initialize-schema=true
spring.ai.vectorstore.pgvector.table-name=vector_store
spring.ai.vectorstore.pgvector.dimension=768  # Adjust based on embedding model

# Flyway Migration
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=validate  # Use Flyway for schema management
spring.jpa.show-sql=false

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379

# Actuator
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
```

---

## Backend Responsibilities

The backend handles:

### Core Functions
- **Domain Execution:** Business logic in modular monolith architecture
- **API Exposure:** REST APIs and Server-Sent Events (SSE)
- **AI Orchestration:** Entry point for AI features (RAG, chat, try-on)
- **Vector Database:** Integration with pgvector for semantic search
- **Authentication/Authorization:** JWT-based security with OAuth2

### Module Structure
```
backend/src/main/java/com/fashion/backend/
├── product/           # Product catalog module
│   ├── domain/       # Business logic
│   ├── application/  # Use cases
│   ├── infrastructure/ # Adapters (JPA, external)
│   └── interfaces/    # REST controllers
├── order/            # Order management module
├── customer/         # Customer management module
├── ai/               # AI features module
└── shared/           # Shared kernel
```

---

## Building and Testing

### Build the Project
```bash
cd backend

# Clean and build (runs tests by default)
./mvnw clean install

# Build without tests
./mvnw clean install -DskipTests
```

### Run Tests
```bash
cd backend

# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=ProductServiceTest

# Run with coverage
./mvnw test jacoco:report
```

### Test Structure
- **Unit Tests:** `*UnitTest.java` (no Spring context)
- **Integration Tests:** `*IntegrationTest.java` (with Spring context)
- **E2E Tests:** `*E2ETest.java` (full system tests)

---

## Database Management

### Flyway Migrations
Migrations are automatically applied on startup. To manage manually:

```bash
cd backend

# Validate migrations
./mvnw flyway:validate

# Info about migrations
./mvnw flyway:info

# Repair if needed
./mvnw flyway:repair
```

### Connect to Database
```bash
# Using psql
psql -h localhost -p 5432 -U fashion_user -d fashion_db_dev

# Using Docker
docker exec -it fashion_postgres psql -U fashion_user -d fashion_db_dev
```

---

## Common Issues & Troubleshooting

### Port 8080 Already in Use
```bash
# Find process
# Windows:
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac:
lsof -i :8080
kill -9 <PID>
```

### Database Connection Failed
1. Check if PostgreSQL is running: `docker-compose ps`
2. Verify credentials in `.env`
3. Check logs: `docker-compose logs postgres`
4. Ensure pgvector extension is installed

### Build Fails with Maven
```bash
# Clean Maven cache
cd backend
./mvnw clean

# Force update dependencies
./mvnw dependency:resolve -U

# Skip tests if needed
./mvnw spring-boot:run -DskipTests=true
```

### Virtual Threads Not Working
- Ensure Java 21+ is installed: `java -version`
- Check `spring.threads.virtual.enabled=true` in config
- Verify no blocking code in critical paths

---

## IDE Setup

### IntelliJ IDEA
1. Open project: `File → Open → Select backend/ folder`
2. Maven will auto-import
3. Set JDK: `File → Project Structure → Project SDK → Java 21`
4. Run configuration: `Spring Boot` main class

### VS Code
1. Install extensions:
   - Extension Pack for Java
   - Spring Boot Extension Pack
2. Open folder: `File → Open Folder → backend/`
3. Run: `F5` or use Spring Boot Dashboard

---

## Profiles

### Development Profile (`dev`)
- H2 console enabled (if configured)
- Detailed logging
- Hot reload enabled (with spring-boot-devtools)

### Production Profile (`prod`)
- Optimized for production
- Minimal logging
- Security hardening enabled

Switch profile:
```bash
SPRING_PROFILES_ACTIVE=prod ./mvnw spring-boot:run
```

---

## API Documentation

Once running, access API documentation:

- **Swagger UI:** http://localhost:8080/api/v1/swagger-ui.html
- **OpenAPI Spec:** http://localhost:8080/api/v1/v3/api-docs

---

## Next Steps

After setup:
1. Read [Architecture Guide](architecture.md) for system design
2. Review [AI Integration](ai-integration.md) for AI features
3. Check [Agent Rules](agent-rules.md) for coding standards
4. Explore the API using Swagger UI

---

## Related Documentation

- [Architecture Guide](architecture.md)
- [AI Integration](ai-integration.md)
- [Agent Rules](agent-rules.md)
- [Concurrency Guide](concurrency.md)
- [Data Privacy](data-privacy.md)
- [Main README](../README.md)
