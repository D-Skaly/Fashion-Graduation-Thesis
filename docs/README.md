# Tài liệu dự án — Fashion Graduation Thesis

Thư mục này là **điểm vào chính** cho người đọc và cho agent (Cursor, v.v.). Luật code và kiến trúc bắt buộc nằm ở [`.junie/AGENTS.md`](../.junie/AGENTS.md); file đó được [`.cursorrules`](../.cursorrules) tham chiếu cho toàn monorepo.

---

## Đọc nhanh theo vai trò

| Vai trò | Đọc trước | Sau đó |
|--------|-----------|--------|
| **Agent / AI** | [`.junie/AGENTS.md`](../.junie/AGENTS.md) → [For-ai.md](For-ai.md) | [architecture/01_system_architecture.md](architecture/01_system_architecture.md) |
| **Dev mới** | [README ở root](../README.md) → [development/getting_started.md](development/getting_started.md) | [development/dev_setup.md](development/dev_setup.md), [development/env-setup.md](development/env-setup.md) |
| **Backend** | [architecture/01_system_architecture.md](architecture/01_system_architecture.md) | [domains/](domains/), [testing/integration-testing.md](testing/integration-testing.md) |
| **Frontend** | [architecture/01_system_architecture.md](architecture/01_system_architecture.md) | [development/coding_standards.md](development/coding_standards.md) |
| **Vận hành** | [operations/infrastructure.md](operations/infrastructure.md) | [operations/deployment_runbook.md](operations/deployment_runbook.md) |

---

## Cấu trúc thư mục `docs/`

| Thư mục / file | Nội dung |
|-----------------|----------|
| [architecture/](architecture/) | Kiến trúc hệ thống, schema DB, chỉ mục ADR |
| [development/](development/) | Cài đặt môi trường, chuẩn code, env |
| [domains/](domains/) | Mô tả theo bounded context (cart, order, product, user, …) |
| [api/](api/) | REST/WebSocket (hợp đồng, sự kiện) |
| [testing/](testing/) | Chiến lược test, kế hoạch, performance |
| [operations/](operations/) | Hạ tầng, triển khai |
| [governance/](governance/) | Quy trình review, chính sách tài liệu |
| [info.md](info.md) | Thiết kế kỹ thuật tổng quan (4-tier, AI, privacy) |
| [For-ai.md](For-ai.md) | PRD/SRS rút gọn, tối ưu cho agent |
| [supabase-rag-quickstart.md](supabase-rag-quickstart.md) | Ghi chú RAG / thử nghiệm (có thể khác môi trường prod) |

---

## Monorepo (ánh xạ với code)

```text
backend/           Spring Boot 3, Spring Modulith, package gốc com.skaly.fashion_backend
frontend/          Next.js (App Router)
ai-orchestrator/   NestJS
ai-service/        FastAPI (xử lý AI nặng)
docs/              Tài liệu (repo này)
nginx/             Cấu hình reverse proxy
docker-compose*.yml
```

---

## Ghi chú cho agent

1. **Không** gọi repository xuyên module; dùng port / event theo AGENTS.md.
2. Domain backend: **Java thuần** (không Spring trong `domain/`).
3. Test tích hợp backend có thể cần **Docker** (PostgreSQL pgvector + Redis qua Testcontainers); xem `backend/src/test/java/.../testsupport/PostgresIntegrationSupport.java` và `application-test.yaml`.
4. Tài liệu có thể song ngữ hoặc tiếng Việt; **chuẩn kiến trúc** lấy từ `.junie/AGENTS.md` làm nguồn đúng khi mâu thuẫn với bản nháp cũ.

---

## Chính sách cập nhật tài liệu

Xem [governance/docs_policy.md](governance/docs_policy.md) (Draft / Active / Deprecated).
