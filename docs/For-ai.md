# [cite_start]📄 AI Fashion Ecosystem — PRD & SRS (Agent-Optimized) [cite: 1]

## [cite_start]1. Tổng quan hệ thống [cite: 2]

### 1.1. [cite_start]Tên hệ thống [cite: 3]
[cite_start]AI Fashion Ecosystem [cite: 4]

### 1.2. [cite_start]Mục tiêu [cite: 5]
[cite_start]Xây dựng nền tảng AI đa agent phục vụ: [cite: 6]
* [cite_start]Tư vấn thời trang cá nhân hóa (User AI Stylist) [cite: 7]
* [cite_start]Hỗ trợ chiến lược kinh doanh (Admin AI Strategist) [cite: 8]

### 1.3. [cite_start]Nguyên tắc thiết kế [cite: 9]
* [cite_start]Clean Architecture (domain độc lập) [cite: 10]
* [cite_start]AI Provider Agnostic (có thể thay model dễ dàng) [cite: 11]
* [cite_start]Privacy-first [cite: 12]
* [cite_start]Async-first cho tác vụ nặng [cite: 13]

---

## [cite_start]2. Actors (Tác nhân hệ thống) [cite: 14]

### 2.1. [cite_start]End User [cite: 15]
* [cite_start]Chat với AI [cite: 16]
* [cite_start]Nhận gợi ý outfit [cite: 17]
* [cite_start]Nhận size recommendation [cite: 18]
* [cite_start]Thực hiện try-on [cite: 19]

### 2.2. [cite_start]Admin [cite: 20]
* [cite_start]Xem dashboard [cite: 21]
* [cite_start]Nhận đề xuất từ AI [cite: 22]
* [cite_start]Phê duyệt / từ chối thay đổi [cite: 23]

### 2.3. [cite_start]AI Agents [cite: 24]
* [cite_start]Stylist Agent (User-facing) [cite: 25]
* [cite_start]Strategist Agent (Admin-facing) [cite: 26]

---

## [cite_start]3. Functional Requirements (FR) [cite: 27]

### [cite_start]FR-01: Chat AI Stylist [cite: 28]
* [cite_start]**Input:** text user [cite: 29]
* [cite_start]**Output:** response + recommended products [cite: 30]
* [cite_start]**Logic:** [cite: 31]
    * [cite_start]User Input → Embedding → Vector Search → Context → LLM → Response [cite: 32]

### [cite_start]FR-02: Product Recommendation (RAG) [cite: 33]
* [cite_start]Sử dụng pgvector [cite: 34]
* [cite_start]Tìm theo semantic, không phải keyword [cite: 35]

### [cite_start]FR-03: Body Measurement (Client-side) [cite: 36]
* [cite_start]Dùng MediaPipe Pose [cite: 37]
* [cite_start]**Output:** 33 landmarks → chuyển thành số đo cơ thể [cite: 38]

### [cite_start]FR-04: Size Recommendation [cite: 39]
* [cite_start]**Input:** body profile [cite: 40]
* [cite_start]**Output:** size + confidence [cite: 41]

### [cite_start]FR-05: Virtual Try-On (Async) [cite: 42]
* [cite_start]**Pipeline:** [cite: 43]
    * [cite_start]Client → Upload → Queue → AI Service → Render → WebSocket → Client [cite: 44]
* [cite_start]**Yêu cầu:** [cite: 45]
    * [cite_start]Không blocking UI [cite: 46]
    * [cite_start]Có trạng thái job [cite: 47]

### [cite_start]FR-06: Admin AI Planning [cite: 48]
* [cite_start]**Input:** yêu cầu (vd: “xả hàng mùa hè”) [cite: 49]
* [cite_start]**Output:** plan dạng draft [cite: 50]

### [cite_start]FR-07: Human-in-the-Loop [cite: 51]
* [cite_start]Mọi thay đổi DB production cần approval [cite: 52]

---

## [cite_start]4. Non-Functional Requirements (NFR) [cite: 53]

### [cite_start]NFR-01: Performance [cite: 54]
* [cite_start]Chat latency < 2s (streaming) [cite: 55]
* [cite_start]Try-on: 5–30s async [cite: 56]

### [cite_start]NFR-02: Scalability [cite: 57]
* [cite_start]Stateless backend [cite: 58]
* [cite_start]Queue-based processing [cite: 59]

### [cite_start]NFR-03: Security [cite: 60]
* [cite_start]API key không lộ ra client [cite: 61]
* [cite_start]Edge làm proxy [cite: 62]

### [cite_start]NFR-04: Privacy [cite: 63]
* [cite_start]Ảnh không lưu lâu [cite: 64]
* [cite_start]Xóa tự động sau xử lý [cite: 65]

---

## [cite_start]5. System Architecture [cite: 66]

### 5.1. [cite_start]Layers [cite: 67]
[cite_start]Client (Next.js) [cite: 68]
[cite_start]↓ [cite: 69]
[cite_start]Edge (Proxy) [cite: 70]
[cite_start]↓ [cite: 71]
[cite_start]Business (Spring Boot) [cite: 72]
[cite_start]↓ [cite: 73]
[cite_start]AI Service (FastAPI) [cite: 74]

### 5.2. [cite_start]Data Flow (Chat) [cite: 75]
* [cite_start]User → Edge → Backend → RAG → LLM → Stream → Client [cite: 76]

### 5.3. [cite_start]Data Flow (Try-On) [cite: 77]
* [cite_start]Client → Queue → Worker → AI Model → Result → WebSocket [cite: 78]

---

## [cite_start]6. Domain Model (Simplified) [cite: 79]

### [cite_start]Entities [cite: 80]
* [cite_start]ChatSession [cite: 81]
* [cite_start]Message [cite: 82]
* [cite_start]Product [cite: 83]
* [cite_start]BodyProfile [cite: 84]
* [cite_start]TryOnJob [cite: 85]
* [cite_start]AdminPlan [cite: 86]

### [cite_start]Example [cite: 87]
* [cite_start]ChatSession [cite: 88]
    * [cite_start]├── messages[] [cite: 89]
    * [cite_start]├── userId [cite: 90]
    * [cite_start]└── context [cite: 91]

---

## [cite_start]7. API Specification (High-level) [cite: 92]

### [cite_start]Chat API [cite: 93]
* [cite_start]`POST /chat` [cite: 94]
    * [cite_start]**body:** `{ message, sessionId }` [cite: 95]
    * [cite_start]**response:** stream [cite: 96]

### [cite_start]Size API [cite: 97]
* [cite_start]`POST /size/recommend` [cite: 98]
    * [cite_start]**body:** `{ bodyProfile }` [cite: 99]
    * [cite_start]**response:** `{ size, confidence }` [cite: 100]

### [cite_start]Try-On API [cite: 101]
* [cite_start]`POST /tryon` [cite: 102]
    * [cite_start]**response:** jobId [cite: 103]
* [cite_start]`GET /tryon/{jobId}` [cite: 104]
    * [cite_start]**response:** status + imageUrl [cite: 105]

### [cite_start]Admin API [cite: 106]
* [cite_start]`POST /admin/plan` [cite: 107]
* [cite_start]`GET /admin/plan/{id}` [cite: 108]
* [cite_start]`POST /admin/approve` [cite: 109]

---

## [cite_start]8. AI Agent Design [cite: 110]

### 8.1. [cite_start]Stylist Agent [cite: 111]
* [cite_start]**Tools:** [cite: 112]
    * [cite_start]`search_products()` [cite: 113]
    * [cite_start]`recommend_size()` [cite: 114]
    * [cite_start]`generate_outfit()` [cite: 115]
* [cite_start]**Flow:** [cite: 116]
    * [cite_start]Intent → Tool selection → Execute → Compose answer [cite: 117]

### 8.2. [cite_start]Strategist Agent [cite: 118]
* [cite_start]**Pattern:** Plan-and-Execute [cite: 119]
* [cite_start]**Flow:** [cite: 120]
    * [cite_start]Goal → Breakdown → Execute → Draft → Await Approval [cite: 121]

---

## [cite_start]9. Data Storage [cite: 122]
* [cite_start]**Database:** PostgreSQL + pgvector [cite: 123, 124]
* [cite_start]**Cache:** Redis (queue + caching) [cite: 125, 126]

---

## [cite_start]10. Tech Stack [cite: 127]

| Layer | Tech |
| :--- | :--- |
| Client | [cite_start]Next.js 14 [cite: 128] |
| Edge | [cite_start]Vercel / Cloudflare [cite: 128] |
| Backend | [cite_start]Spring Boot [cite: 128] |
| AI Service | [cite_start]FastAPI [cite: 128] |
| [cite_start]Vector DB | pgvector [cite: 128] |
| Queue | [cite_start]Redis [cite: 128] |

---

## [cite_start]11. Roadmap (Execution Plan) [cite: 129]
* [cite_start]**Phase 1 (Week 1–2):** Chat + RAG [cite: 130, 131]
* [cite_start]**Phase 2 (Week 3–4):** Body + Size [cite: 132, 133]
* [cite_start]**Phase 3 (Week 5–6):** Try-On Async [cite: 134, 135]
* [cite_start]**Phase 4 (Week 7–8):** Admin Dashboard + HITL [cite: 136, 137]

---

## [cite_start]12. Definition of Done [cite: 138]
* [cite_start]Chat hoạt động + semantic search [cite: 139]
* [cite_start]Size recommendation chạy được [cite: 140]
* [cite_start]Try-on async hoạt động [cite: 141]
* [cite_start]Admin phải approve trước khi thay đổi DB [cite: 142]
* [cite_start]Có thể đổi AI model qua adapter [cite: 143]

---

## [cite_start]13. Agent Execution Instructions (Quan trọng nhất) [cite: 144]
[cite_start]AI agent khi đọc tài liệu này phải tuân thủ: [cite: 145]
* [cite_start]**RULE-1:** Không viết logic AI trực tiếp vào domain [cite: 146]
* [cite_start]**RULE-2:** Mọi AI phải đi qua interface (port) [cite: 147]
* [cite_start]**RULE-3:** Tác vụ nặng phải async [cite: 148]
* [cite_start]**RULE-4:** Không lưu dữ liệu nhạy cảm lâu [cite: 149]
* [cite_start]**RULE-5:** Admin actions phải có approval [cite: 150]
* [cite_start]**RULE-6:** Ưu tiên triển khai theo roadmap [cite: 151]

---

## [cite_start]14. Task Breakdown (Cho AI agent) [cite: 152]

### [cite_start]Sprint 1 [cite: 153]
* [cite_start]Setup project [cite: 154]
* [cite_start]Chat API [cite: 155]
* [cite_start]RAG [cite: 156]

### [cite_start]Sprint 2 [cite: 157]
* [cite_start]Body detection [cite: 158]
* [cite_start]Size logic [cite: 159]

### [cite_start]Sprint 3 [cite: 160]
* [cite_start]Queue + Try-on [cite: 161]

### [cite_start]Sprint 4 [cite: 162]
* [cite_start]Admin AI + approval system [cite: 163]

---

## [cite_start]15. Tóm tắt cho AI [cite: 164]
[cite_start]Build hệ thống AI thời trang với kiến trúc 4 lớp, bắt đầu từ chat + RAG, sau đó thêm body sizing, tiếp theo là try-on async, cuối cùng là admin AI có kiểm soát. [cite: 165]
[cite_start]Luôn giữ clean architecture và privacy-first. [cite: 166]