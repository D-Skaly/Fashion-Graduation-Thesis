# Fashion E-Commerce System (Monorepo)

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.x-green.svg)](https://spring.io/projects/spring-boot)
[![Next.js](https://img.shields.io/badge/Next.js-14-black.svg)](https://nextjs.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

Graduation thesis project: Modern e-commerce fashion platform built with **Spring Modulith**, **Next.js**, and integrated AI capabilities (RAG, assistant, virtual try-on).

📚 **Documentation**: [**docs/README.md**](docs/README.md) - Complete documentation hub  
🤖 **AI Agents**: [`.junie/AGENTS.md`](.junie/AGENTS.md) - Mandatory coding standards

---

## 📋 Table of Contents

- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Requirements](#-requirements)
- [Quick Start](#-quick-start)
- [Detailed Setup](#-detailed-setup)
- [Available Scripts](#-available-scripts)
- [Environment Variables](#-environment-variables)
- [Testing](#-testing)
- [Documentation](#-documentation)
- [Troubleshooting](#-troubleshooting)
- [Contributing](#-contributing)
- [License](#-license)

---

## 🚀 Tech Stack

### Backend
- **Language:** Java 21 (LTS)
- **Framework:** Spring Boot 3.3.x
- **Architecture:** Spring Modulith (Modular Monolith)
- **AI Integration:** Spring AI 1.0
- **Database:** PostgreSQL 15+ with `pgvector` extension
- **Caching:** Redis 7
- **Security:** Spring Security, OAuth2, JWT
- **Migration:** Flyway
- **Build Tool:** Maven (with Maven Wrapper)

### Frontend
- **Framework:** Next.js 14 (App Router)
- **UI Components:** Shadcn UI, Tailwind CSS, Lucide React
- **State Management:** TanStack Query (React Query)
- **Language:** TypeScript
- **Package Manager:** npm

### AI Orchestrator
- **Framework:** NestJS
- **Language:** TypeScript
- **Runtime:** Node.js 20+

### AI Service
- **Framework:** FastAPI
- **Language:** Python 3.11+
- **AI Libraries:** PyTorch, OpenCV, etc.

### Infrastructure
- **Containerization:** Docker, Docker Compose
- **Web Server:** Nginx (reverse proxy, SSL termination)
- **Object Storage:** MinIO (S3-compatible)
- **Monitoring:** (Planned) Prometheus + Grafana

---

## 📁 Project Structure

```text
.
├── backend/                # Spring Boot 3 application (Core API, Java 21)
│   ├── src/main/java/     # Java source code
│   ├── src/test/java/      # Tests
│   └── pom.xml             # Maven configuration
├── frontend/               # Next.js web application (App Router)
│   ├── src/               # Source code
│   ├── public/            # Static assets
│   └── package.json       # npm configuration
├── ai-orchestrator/        # NestJS AI orchestration layer
│   ├── src/               # Source code
│   └── package.json       # npm configuration
├── ai-service/             # FastAPI — heavy AI tasks (try-on pipeline)
│   ├── app/               # Application code
│   └── requirements.txt   # Python dependencies
├── nginx/                  # Nginx configuration for reverse proxy
│   └── conf/              # Configuration files
├── docs/                   # Documentation (see docs/README.md)
│   ├── architecture.md    # System design
│   ├── backend.md         # Backend setup
│   ├── ai-integration.md  # AI patterns
│   └── ...                # More docs
├── docker-compose.yml      # Development infrastructure (DB, Redis)
├── docker-compose.prod.yml # Production deployment configuration
├── DEPLOY.md               # Production deployment guide
└── README.md               # This file
```

---

## 🛠️ Requirements

### Minimum Requirements
- **Java 21** (LTS) - [Download](https://www.oracle.com/java/technologies/downloads/)
- **Node.js 20+** - [Download](https://nodejs.org/)
- **Docker Desktop** - [Download](https://www.docker.com/products/docker-desktop/)
- **Maven 3.9+** (or use included `mvnw` wrapper)

### Recommended Tools
- **IDE:** IntelliJ IDEA (Backend), VS Code (Frontend)
- **Postman** or **cURL** for API testing
- **Git** for version control

---

## ⚡ Quick Start

Get the application running in 3 steps:

```bash
# 1. Clone the repository
git clone <repository-url>
cd Fashion-Graduation-Thesis

# 2. Start infrastructure (PostgreSQL + Redis)
docker-compose up -d

# 3. Start all services (in separate terminals)
cd backend && ./mvnw spring-boot:run
cd frontend && npm install && npm run dev
```

Open [http://localhost:3000](http://localhost:3000) in your browser.

---

## ⚙️ Detailed Setup

### 1. Infrastructure (Database & Redis)

The easiest way to start is using Docker Compose:

```bash
docker-compose up -d
```

This starts:
- **PostgreSQL** (Port 5432) with pgvector extension
- **Redis** (Port 6379) for caching

Verify services are running:
```bash
docker-compose ps
```

### 2. Backend Setup

```bash
cd backend

# Copy environment file (Windows)
copy .env.dev .env

# Copy environment file (Linux/Mac)
cp .env.dev .env

# Edit .env with your credentials if needed
# Then run the application
./mvnw spring-boot:run
```

Backend will be available at: http://localhost:8080

**Note:** First run may take longer as Maven downloads dependencies.

### 3. Frontend Setup

```bash
cd frontend

# Install dependencies
npm install

# Run development server
npm run dev
```

Frontend will be available at: http://localhost:3000

### 4. AI Services Setup (Optional)

For full AI features, also start:

```bash
# AI Orchestrator (NestJS)
cd ai-orchestrator
npm install
npm run start:dev

# AI Service (FastAPI)
cd ai-service
pip install -r requirements.txt
uvicorn app.main:app --reload
```

---

## 📜 Available Scripts

### Backend (`/backend`)

| Command | Description |
|---------|-------------|
| `./mvnw clean install` | Build the project and run tests |
| `./mvnw spring-boot:run` | Run the application |
| `./mvnw test` | Run unit and integration tests |
| `./mvnw spring-boot:run -Dspring.profiles.active=prod` | Run with production profile |

### Frontend (`/frontend`)

| Command | Description |
|---------|-------------|
| `npm run dev` | Start development server with hot reload |
| `npm run build` | Build production application |
| `npm run start` | Start production server |
| `npm run lint` | Run ESLint for code quality |
| `npm run lint:fix` | Fix ESLint errors automatically |

### AI Orchestrator (`/ai-orchestrator`)

| Command | Description |
|---------|-------------|
| `npm run start:dev` | Start NestJS in watch mode |
| `npm run build` | Build for production |
| `npm run start:prod` | Start production server |

### AI Service (`/ai-service`)

| Command | Description |
|---------|-------------|
| `uvicorn app.main:app --reload` | Start FastAPI with auto-reload |
| `pytest` | Run Python tests |
| `pip install -r requirements.txt` | Install dependencies |

---

## 🔑 Environment Variables

### Backend Key Variables (`.env` file)

| Variable | Description | Default |
|----------|-------------|---------|
| `DATABASE_URL` | PostgreSQL connection string | `jdbc:postgresql://localhost:5432/fashion_db_dev` |
| `DATABASE_USERNAME` | Database username | `fashion_user` |
| `DATABASE_PASSWORD` | Database password | `fashion_pass` |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `dev` |
| `JWT_SECRET_KEY` | Secret key for JWT signing | (Auto-generated) |
| `GEMINI_API_KEY` | Google Gemini API Key | `TODO: Add Key` |
| `AI_ENABLED` | Toggle AI features | `true` |
| `REDIS_HOST` | Redis server host | `localhost` |
| `REDIS_PORT` | Redis server port | `6379` |

### Frontend Key Variables (`.env.local` file)

| Variable | Description | Default |
|----------|-------------|---------|
| `NEXT_PUBLIC_API_BASE_URL` | Backend API URL | `http://localhost:8080/api/v1` |
| `NEXT_PUBLIC_APP_URL` | Frontend app URL | `http://localhost:3000` |

### AI Service Key Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `GEMINI_API_KEY` | Google Gemini API Key | Required |
| `OPENAI_API_KEY` | OpenAI API Key (optional) | Optional |

---

## 🧪 Testing

### Backend Testing

We use **JUnit 5** and **Spring Boot Test** for verification:

```bash
cd backend

# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=ProductServiceTest

# Run with coverage report
./mvnw test jacoco:report
```

**Note:** Some integration tests require Docker containers to be running (PostgreSQL, Redis).

#### Test Structure
- `*UnitTest.java` - Pure unit tests (no Spring context)
- `*IntegrationTest.java` - Integration tests (with Spring context)
- `*E2ETest.java` - End-to-end tests (full system)

### Frontend Testing

```bash
cd frontend

# Lint check
npm run lint

# Type check
npm run type-check

# Unit tests (when configured)
npm run test

# E2E tests with Playwright (when configured)
npm run test:e2e
```

### AI Services Testing

```bash
# AI Orchestrator
cd ai-orchestrator
npm run test

# AI Service
cd ai-service
pytest
```

---

## 📖 Documentation

All detailed documentation is organized in the [`docs/`](docs/) folder.

### 📚 Documentation Hub
**[docs/README.md](docs/README.md)** - Start here for complete navigation

### Core Documentation

| Document | Description |
|----------|-------------|
| [Architecture](docs/architecture.md) | System design, module structure, core principles |
| [Backend Setup](docs/backend.md) | Configuration, prerequisites, responsibilities |
| [AI Integration](docs/ai-integration.md) | Spring AI patterns, RAG, MCP, design rules |
| [Concurrency](docs/concurrency.md) | Java 21 virtual threads, structured concurrency |
| [Data Privacy](docs/data-privacy.md) | Privacy rules, streaming, evaluation, observability |
| [Agent Rules](docs/agent-rules.md) | Mandatory coding standards for AI agents |
| [Do's & Don'ts](docs/do-dont.md) | Engineering guidelines and best practices |
| [Prompt Patterns](docs/prompt-patterns.md) | AI prompt engineering patterns |

### Additional Resources
- **Deployment Guide:** [DEPLOY.md](DEPLOY.md) - Production deployment
- **Agent Guide:** [`.junie/AGENTS.md`](.junie/AGENTS.md) - Mandatory for AI agents
- **ADR Index:** [docs/adr/README.md](docs/adr/README.md) - Architecture Decision Records

---

## 🔧 Troubleshooting

### Common Issues

#### Port Already in Use
```bash
# Find process using port
# Windows:
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac:
lsof -i :8080
kill -9 <PID>
```

#### Docker Containers Not Starting
```bash
# Check Docker logs
docker-compose logs

# Restart containers
docker-compose down
docker-compose up -d

# Check if ports are available
docker-compose ps
```

#### Backend Build Fails
```bash
# Clean Maven cache
cd backend
./mvnw clean

# Re-download dependencies
./mvnw dependency:resolve

# Skip tests if needed
./mvnw spring-boot:run -Dskip.tests=true
```

#### Frontend npm Install Fails
```bash
cd frontend

# Clear npm cache
npm cache clean --force

# Delete node_modules and reinstall
rm -rf node_modules package-lock.json
npm install
```

#### Database Connection Issues
1. Verify PostgreSQL is running: `docker-compose ps`
2. Check credentials in `.env` file
3. Ensure pgvector extension is installed
4. Check logs: `docker-compose logs postgres`

#### AI Features Not Working
1. Verify `AI_ENABLED=true` in backend `.env`
2. Check API keys (Gemini/OpenAI) are set correctly
3. Ensure AI services are running
4. Check logs for AI service errors

### Getting Help

If you encounter issues not listed here:
1. Check the [Documentation Hub](docs/README.md)
2. Search existing [GitHub Issues](../../issues)
3. Create a new issue with detailed error information

---

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

### Development Workflow

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

### Code Standards

- Follow the [Agent Rules](docs/agent-rules.md) for code generation
- Adhere to [Do's & Don'ts](docs/do-dont.md) guidelines
- Write tests for new features
- Update documentation as needed
- Ensure all tests pass before submitting PR

### Documentation Contributions

When adding or updating documentation:
1. Place new docs in `docs/` directory
2. Use clear, descriptive filenames (kebab-case)
3. Update `docs/README.md` with new entries
4. Include code examples where relevant
5. Follow existing markdown structure

### Pull Request Checklist

- [ ] Code follows project standards
- [ ] Tests added/updated for changes
- [ ] Documentation updated if needed
- [ ] All tests passing
- [ ] No new warnings or errors

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- Spring Modulith team for the excellent modular monolith framework
- Spring AI team for AI integration abstractions
- Next.js team for the fantastic React framework
- All open-source libraries used in this project

---

## 📧 Contact

For questions or support, please contact the development team or open an issue in the repository.

**Project Link:** [https://github.com/yourusername/Fashion-Graduation-Thesis](https://github.com/yourusername/Fashion-Graduation-Thesis)

---

<p align="center">
  Built with ❤️ for modern e-commerce fashion experiences
</p>
