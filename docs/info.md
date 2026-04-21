# 📄 AI Fashion Ecosystem — Technical Design Document

> **Mục lục & hướng đọc:** [docs/README.md](README.md) — điểm vào chính cho người và agent.

Tài liệu này xác định kiến trúc, công nghệ và quy trình xử lý cho hệ thống đại lý AI (AI Agents) đa nhiệm, hỗ trợ tư vấn khách hàng cá nhân hóa và quản trị chiến lược kinh doanh thông minh.

---

## 1. Kiến trúc Hệ thống đa lớp (4-Tier Architecture)

Để đảm bảo tính **Neutrality** (không phụ thuộc nhà cung cấp AI) và **Clean Architecture**, hệ thống được chia thành 4 lớp:

| Lớp | Công nghệ | Vai trò chính |
| :--- | :--- | :--- |
| **Client Layer** | Next.js (App Router) + Transformers.js | Chạy mô hình AI nhẹ (MediaPipe, Quantized Models) ngay tại trình duyệt để bảo vệ quyền riêng tư và giảm tải Server. |
| **Edge Layer** | Edge Functions (Vercel/Cloudflare) | Đóng vai trò Secure Proxy để streaming kết quả từ LLM về Client nhanh chóng và bảo mật API Key. |
| **Business Layer** | Spring Boot 3.x | Core Domain logic, điều phối các Use Cases, quản lý giao dịch và bảo mật. |
| **AI Service Layer** | Python (FastAPI) | Xử lý các tác vụ nặng: Virtual Try-On (IDM-VTON) và Vision chuyên sâu. |

---

## 2. Thiết kế Domain & Business Logic (Clean Architecture)

Cấu trúc mã nguồn tại Backend Spring Boot tuân thủ nghiêm ngặt tính độc lập:

* **Domain Layer (Pure Java):** Chứa Entities (*ChatSession*, *BodyProfile*) và Ports (*AIModelPort*). Lớp này hoàn toàn độc lập, không phụ thuộc vào các nhà cung cấp cụ thể như OpenAI hay Claude.
* **Application Layer:** Điều phối các Interactors (Ví dụ: *RecommendProductInteractor*).
* **Infrastructure Layer:** Các Adapters cụ thể cho PostgreSQL (pgvector), Gemini API, hoặc Redis Task Queue.

---

## 3. Chiến lược AI Agents chuyên biệt

### A. User AI Stylist (Cá nhân hóa & Trải nghiệm)
* **RAG (Retrieval-Augmented Generation):** Sử dụng `pgvector` trên PostgreSQL để tìm kiếm sản phẩm theo ngữ nghĩa.
* **Smart Sizing (Split Inference):** * **Tại Client:** MediaPipe Pose lấy 33 điểm mốc cơ thể và tính toán thông số thô.
    * **Tại Server:** Logic Domain ánh xạ thông số vào bảng Size để đưa ra đề xuất chính xác.
* **Virtual Try-On (Async Pipeline):** Xử lý bất đồng bộ qua Redis Queue. Người dùng nhận kết quả qua WebSocket sau khi GPU Render xong (khoảng 5-30 giây).

### B. Admin AI Strategist (Trung thực & Hiệu quả)
* **Agentic Workflow:** Sử dụng mẫu "Plan-and-Execute" để AI tự chia nhỏ nhiệm vụ từ yêu cầu của Admin.
* **Cơ chế HITL (Human-in-the-Loop):** AI chỉ tạo bản nháp (DRAFT). Các thay đổi về giá hoặc chiến lược bắt buộc phải có sự phê duyệt của Admin mới được cập nhật vào DB sản xuất.

---

## 4. Giải pháp Kỹ thuật tối ưu chi phí (College Project Scale)

Giải pháp triển khai với chi phí tối ưu mà vẫn đảm bảo tiêu chuẩn kỹ thuật cao:
* **Lượng tử hóa (Quantization):** Nén mô hình nhận diện body (~30MB) để chạy trực tiếp trên trình duyệt bằng `Transformers.js`.
* **Mô hình LLM:** Sử dụng `Gemini 1.5 Flash` (Free Tier) cho khả năng tư vấn và Tool-calling.
* **Hạ tầng Vector:** Sử dụng gói miễn phí của `Supabase` (Postgres + pgvector).
* **Try-On GPU:** Tận dụng Gradio Client để gọi API miễn phí từ Hugging Face (mô hình IDM-VTON).

---

## 5. Bảo mật & Quyền riêng tư (Privacy-First)

* **Ảnh người dùng:** Chỉ xử lý tại Client hoặc sử dụng **Presigned URL** với thời hạn < 1 giờ trên Cloudflare R2.
* **Tự động xóa:** Thiết lập chính sách Lifecycle tự động xóa ảnh gốc ngay sau khi xử lý Try-On xong.

---

## 6. Lộ trình triển khai (Roadmap)

* **Giai đoạn 1 (Tuần 1-2):** Thiết lập WebSocket Chat và RAG cơ bản với pgvector.
* **Giai đoạn 2 (Tuần 3-4):** Tích hợp nhận diện Body tại Client và module tư vấn Size.
* **Giai đoạn 3 (Tuần 5-6):** Triển khai Virtual Try-On bất đồng bộ qua Queue.
* **Giai đoạn 4 (Tuần 7-8):** Xây dựng Dashboard Admin AI với quy trình phê duyệt HITL.

> **Senior Tip:** Hãy nhấn mạnh khả năng thay đổi "bộ não" AI linh hoạt qua việc cấu hình lại Adapter làm minh chứng cho tư duy Clean Architecture.