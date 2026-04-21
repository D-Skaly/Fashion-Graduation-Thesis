# Fashion E-Commerce System (Monorepo)

Graduation thesis: e-commerce thời trang với **Spring Modulith**, **Next.js**, tích hợp AI (RAG, assistant, try-on). Tài liệu tập trung tại **[`docs/README.md`](docs/README.md)**; hướng dẫn cho agent và dev: **[`.junie/AGENTS.md`](.junie/AGENTS.md)** (được [`.cursorrules`](.cursorrules) tham chiếu).

---

## 🚀 Tech Stack

### Backend
- **Framework:** Spring Boot 3.5.x
- **Architecture:** Spring Modulith (Modular Monolith)
- **AI Integration:** Spring AI (Google Gemini, PGVector)
- **Database:** PostgreSQL 15+ with `pgvector`
- **Caching:** Redis 7
- **Security:** Spring Security, OAuth2, JWT
- **Migration:** Flyway
- **Build Tool:** Maven

### Frontend
- **Framework:** Next.js 16 (App Router)
- **UI Components:** Shadcn UI, Tailwind CSS, Lucide React
- **State Management:** TanStack Query (React Query)
- **Language:** TypeScript

### AI Orchestrator
- **Framework:** NestJS
- **Language:** TypeScript

### Infrastructure
- **Containerization:** Docker, Docker Compose
- **Web Server:** Nginx
- **Object Storage:** MinIO

---

## 📁 Project Structure

```text
.
├── backend/            # Spring Boot 3 application (Core API)
├── frontend/           # Next.js web application (App Router)
├── ai-orchestrator/    # NestJS AI orchestration layer
├── ai-service/         # FastAPI — tác vụ AI nặng (vd. try-on pipeline)
├── nginx/              # Nginx configuration for reverse proxy
├── docs/               # Mục lục: docs/README.md + architecture, API, testing
├── docker-compose.yml  # Development infrastructure (DB, Redis)
└── docker-compose.prod.yml # Production deployment configuration
```

---

## 🛠️ Requirements

- **Java 21** (LTS)
- **Node.js 20+**
- **Docker Desktop**
- **Maven 3.9+** (or use included `mvnw`)

---

## ⚙️ Setup & Installation

### 1. Infrastructure (Database & Redis)
The easiest way to start is using Docker Compose to spin up the required services:

```bash
docker-compose up -d
```
This starts:
- PostgreSQL (Port 5432)
- Redis (Port 6379)

### 2. Backend Setup
1. Navigate to the backend directory:
   ```bash
   cd backend
   ```
2. Copy `.env.dev` to `.env` (if not already present) and update your credentials:
   ```bash
   # Windows
   copy .env.dev .env
   # Linux/Mac
   cp .env.dev .env
   ```
3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

### 3. Frontend Setup
1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Run the development server:
   ```bash
   npm run dev
   ```
   Open [http://localhost:3000](http://localhost:3000) in your browser.

---

## 📜 Available Scripts

### Backend (`/backend`)
- `./mvnw clean install`: Build the project and run tests.
- `./mvnw spring-boot:run`: Run the application.
- `./mvnw test`: Run unit and integration tests.

### Frontend (`/frontend`)
- `npm run dev`: Start development server.
- `npm run build`: Build production application.
- `npm run start`: Start production server.
- `npm run lint`: Run ESLint.

### AI Orchestrator (`/ai-orchestrator`)
- `npm run start:dev`: Start NestJS in watch mode.

---

## 🔑 Environment Variables

### Backend Key Variables
| Variable | Description | Default |
|----------|-------------|---------|
| `DATABASE_URL` | PostgreSQL connection string | `jdbc:postgresql://localhost:5432/fashion_db_dev` |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `dev` |
| `JWT_SECRET_KEY` | Secret key for JWT signing | (Generated) |
| `GEMINI_API_KEY` | Google Gemini API Key | TODO: Add Key |
| `AI_ENABLED` | Toggle AI features | `true` |

### Frontend Key Variables
| Variable | Description | Default |
|----------|-------------|---------|
| `NEXT_PUBLIC_API_BASE_URL` | Backend API URL | `http://localhost:8080/api/v1` |

---

## 🧪 Testing

### Backend
We use JUnit 5 and Spring Boot Test for verification:
```bash
cd backend
./mvnw test
```
*Note: Some tests require the Docker containers to be running (PostgreSQL).*

### Frontend
```bash
cd frontend
npm run lint
```
*(TODO: Add Playwright/Jest testing instructions)*

---

## 📖 Documentation

- **Mục lục & lộ trình đọc:** [docs/README.md](docs/README.md)
- **Quy tắc kiến trúc / agent:** [.junie/AGENTS.md](.junie/AGENTS.md)
- [System Architecture](docs/architecture/01_system_architecture.md)
- [Database Schema](docs/architecture/02_database_schema.md)
- [Coding Standards](docs/development/coding_standards.md)
- [Getting Started Guide](docs/development/getting_started.md)

---

## 📄 License

Xem file [LICENSE](LICENSE) trong repo (nếu có).
