# 📄 AI Fashion Ecosystem — PRD & SRS (Agent-Optimized)

> Mục lục tài liệu & liên kết kiến trúc: [docs/README.md](README.md). Quy tắc triển khai bắt buộc: [`.junie/AGENTS.md`](../.junie/AGENTS.md).

## 1. Tổng quan hệ thống 

### 1.1. Tên hệ thống 
AI Fashion Ecosystem 

### 1.2. Mục tiêu 
Xây dựng nền tảng AI đa agent phục vụ: 
* Tư vấn thời trang cá nhân hóa (User AI Stylist) 
* Hỗ trợ chiến lược kinh doanh (Admin AI Strategist) 

### 1.3. Nguyên tắc thiết kế 
* Clean Architecture (domain độc lập) 
* AI Provider Agnostic (có thể thay model dễ dàng) 
* Privacy-first 
* Async-first cho tác vụ nặng 

---

## 2. Actors (Tác nhân hệ thống) 

### 2.1. End User 
* Chat với AI 
* Nhận gợi ý outfit 
* Nhận size recommendation 
* Thực hiện try-on 

### 2.2. Admin 
* Xem dashboard 
* Nhận đề xuất từ AI 
* Phê duyệt / từ chối thay đổi 

### 2.3. AI Agents 
* Stylist Agent (User-facing) 
* Strategist Agent (Admin-facing) 

---

## 3. Functional Requirements (FR) 

### FR-01: Chat AI Stylist 
* **Input:** text user 
* **Output:** response + recommended products 
* **Logic:** 
    * User Input → Embedding → Vector Search → Context → LLM → Response 

### FR-02: Product Recommendation (RAG) 
* Sử dụng pgvector 
* Tìm theo semantic, không phải keyword 

### FR-03: Body Measurement (Client-side) 
* Dùng MediaPipe Pose 
* **Output:** 33 landmarks → chuyển thành số đo cơ thể 

### FR-04: Size Recommendation 
* **Input:** body profile 
* **Output:** size + confidence 

### FR-05: Virtual Try-On (Async) 
* **Pipeline:** 
    * Client → Upload → Queue → AI Service → Render → WebSocket → Client 
* **Yêu cầu:** 
    * Không blocking UI 
    * Có trạng thái job 

### FR-06: Admin AI Planning 
* **Input:** yêu cầu (vd: “xả hàng mùa hè”) 
* **Output:** plan dạng draft 

### FR-07: Human-in-the-Loop 
* Mọi thay đổi DB production cần approval 

---

## 4. Non-Functional Requirements (NFR) 

### NFR-01: Performance 
* Chat latency < 2s (streaming) 
* Try-on: 5–30s async 

### NFR-02: Scalability 
* Stateless backend 
* Queue-based processing 

### NFR-03: Security 
* API key không lộ ra client 
* Edge làm proxy 

### NFR-04: Privacy 
* Ảnh không lưu lâu 
* Xóa tự động sau xử lý 

---

## 5. System Architecture 

### 5.1. Layers 
Client (Next.js) 
↓ 
Edge (Proxy) 
↓ 
Business (Spring Boot) 
↓ 
AI Service (FastAPI) 

### 5.2. Data Flow (Chat) 
* User → Edge → Backend → RAG → LLM → Stream → Client 

### 5.3. Data Flow (Try-On) 
* Client → Queue → Worker → AI Model → Result → WebSocket 

---

## 6. Domain Model (Simplified) 

### Entities 
* ChatSession 
* Message 
* Product 
* BodyProfile 
* TryOnJob 
* AdminPlan 

### Example 
* ChatSession 
    * ├── messages[] 
    * ├── userId 
    * └── context 

---

## 7. API Specification (High-level) 

### Chat API 
* `POST /chat` 
    * **body:** `{ message, sessionId }` 
    * **response:** stream 

### Size API 
* `POST /size/recommend` 
    * **body:** `{ bodyProfile }` 
    * **response:** `{ size, confidence }` 

### Try-On API 
* `POST /tryon` 
    * **response:** jobId 
* `GET /tryon/{jobId}` 
    * **response:** status + imageUrl 

### Admin API 
* `POST /admin/plan` 
* `GET /admin/plan/{id}` 
* `POST /admin/approve` 

---

## 8. AI Agent Design 

### 8.1. Stylist Agent 
* **Tools:** 
    * `search_products()` 
    * `recommend_size()` 
    * `generate_outfit()` 
* **Flow:** 
    * Intent → Tool selection → Execute → Compose answer 

### 8.2. Strategist Agent 
* **Pattern:** Plan-and-Execute 
* **Flow:** 
    * Goal → Breakdown → Execute → Draft → Await Approval 

---

## 9. Data Storage 
* **Database:** PostgreSQL + pgvector 
* **Cache:** Redis (queue + caching) 

---

## 10. Tech Stack 

| Layer | Tech |
| :--- | :--- |
| Client | Next.js (App Router) |
| Edge | Vercel / Cloudflare  |
| Backend | Spring Boot  |
| AI Service | FastAPI  |
| Vector DB | pgvector  |
| Queue | Redis  |

---

## 11. Roadmap (Execution Plan) 
* **Phase 1 (Week 1–2):** Chat + RAG 
* **Phase 2 (Week 3–4):** Body + Size 
* **Phase 3 (Week 5–6):** Try-On Async 
* **Phase 4 (Week 7–8):** Admin Dashboard + HITL 

---

## 12. Definition of Done 
* Chat hoạt động + semantic search 
* Size recommendation chạy được 
* Try-on async hoạt động 
* Admin phải approve trước khi thay đổi DB 
* Có thể đổi AI model qua adapter 

---

## 13. Agent Execution Instructions (Quan trọng nhất) 
AI agent khi đọc tài liệu này phải tuân thủ: 
* **RULE-1:** Không viết logic AI trực tiếp vào domain 
* **RULE-2:** Mọi AI phải đi qua interface (port) 
* **RULE-3:** Tác vụ nặng phải async 
* **RULE-4:** Không lưu dữ liệu nhạy cảm lâu 
* **RULE-5:** Admin actions phải có approval 
* **RULE-6:** Ưu tiên triển khai theo roadmap 

---

## 14. Task Breakdown (Cho AI agent) 

### Sprint 1 
* Setup project 
* Chat API 
* RAG 

### Sprint 2 
* Body detection 
* Size logic 

### Sprint 3 
* Queue + Try-on 

### Sprint 4 
* Admin AI + approval system 

---

## 15. Tóm tắt cho AI 
Build hệ thống AI thời trang với kiến trúc 4 lớp, bắt đầu từ chat + RAG, sau đó thêm body sizing, tiếp theo là try-on async, cuối cùng là admin AI có kiểm soát. 
Luôn giữ clean architecture và privacy-first. 