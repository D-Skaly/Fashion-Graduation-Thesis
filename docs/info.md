# 📄 AI Fashion Ecosystem — Technical Design Document

[cite_start]Tài liệu này xác định kiến trúc, công nghệ và quy trình xử lý cho hệ thống đại lý AI (AI Agents) đa nhiệm, hỗ trợ tư vấn khách hàng cá nhân hóa và quản trị chiến lược kinh doanh thông minh[cite: 168].

---

## 1. Kiến trúc Hệ thống đa lớp (4-Tier Architecture)

[cite_start]Để đảm bảo tính **Neutrality** (không phụ thuộc nhà cung cấp AI) và **Clean Architecture**, hệ thống được chia thành 4 lớp[cite: 170]:

| Lớp | Công nghệ | Vai trò chính |
| :--- | :--- | :--- |
| **Client Layer** | Next.js 14 + Transformers.js | [cite_start]Chạy mô hình AI nhẹ (MediaPipe, Quantized Models) ngay tại trình duyệt để bảo vệ quyền riêng tư và giảm tải Server[cite: 171]. |
| **Edge Layer** | Edge Functions (Vercel/Cloudflare) | [cite_start]Đóng vai trò Secure Proxy để streaming kết quả từ LLM về Client nhanh chóng và bảo mật API Key[cite: 171]. |
| **Business Layer** | Spring Boot 3.x | [cite_start]Core Domain logic, điều phối các Use Cases, quản lý giao dịch và bảo mật[cite: 171]. |
| **AI Service Layer** | Python (FastAPI) | [cite_start]Xử lý các tác vụ nặng: Virtual Try-On (IDM-VTON) và Vision chuyên sâu[cite: 171]. |

---

## 2. Thiết kế Domain & Business Logic (Clean Architecture)

[cite_start]Cấu trúc mã nguồn tại Backend Spring Boot tuân thủ nghiêm ngặt tính độc lập[cite: 173]:

* [cite_start]**Domain Layer (Pure Java):** Chứa Entities (*ChatSession*, *BodyProfile*) và Ports (*AIModelPort*)[cite: 174]. [cite_start]Lớp này hoàn toàn độc lập, không phụ thuộc vào các nhà cung cấp cụ thể như OpenAI hay Claude[cite: 175].
* [cite_start]**Application Layer:** Điều phối các Interactors (Ví dụ: *RecommendProductInteractor*)[cite: 176].
* [cite_start]**Infrastructure Layer:** Các Adapters cụ thể cho PostgreSQL (pgvector), Gemini API, hoặc Redis Task Queue[cite: 177].

---

## 3. Chiến lược AI Agents chuyên biệt

### A. User AI Stylist (Cá nhân hóa & Trải nghiệm)
* [cite_start]**RAG (Retrieval-Augmented Generation):** Sử dụng `pgvector` trên PostgreSQL để tìm kiếm sản phẩm theo ngữ nghĩa[cite: 180].
* [cite_start]**Smart Sizing (Split Inference):** * **Tại Client:** MediaPipe Pose lấy 33 điểm mốc cơ thể và tính toán thông số thô[cite: 182].
    * [cite_start]**Tại Server:** Logic Domain ánh xạ thông số vào bảng Size để đưa ra đề xuất chính xác[cite: 183].
* **Virtual Try-On (Async Pipeline):** Xử lý bất đồng bộ qua Redis Queue. [cite_start]Người dùng nhận kết quả qua WebSocket sau khi GPU Render xong (khoảng 5-30 giây)[cite: 184, 185].

### B. Admin AI Strategist (Trung thực & Hiệu quả)
* [cite_start]**Agentic Workflow:** Sử dụng mẫu "Plan-and-Execute" để AI tự chia nhỏ nhiệm vụ từ yêu cầu của Admin[cite: 187].
* [cite_start]**Cơ chế HITL (Human-in-the-Loop):** AI chỉ tạo bản nháp (DRAFT)[cite: 188]. [cite_start]Các thay đổi về giá hoặc chiến lược bắt buộc phải có sự phê duyệt của Admin mới được cập nhật vào DB sản xuất[cite: 189].

---

## 4. Giải pháp Kỹ thuật tối ưu chi phí (College Project Scale)

[cite_start]Giải pháp triển khai với chi phí tối ưu mà vẫn đảm bảo tiêu chuẩn kỹ thuật cao[cite: 191]:
* [cite_start]**Lượng tử hóa (Quantization):** Nén mô hình nhận diện body (~30MB) để chạy trực tiếp trên trình duyệt bằng `Transformers.js`[cite: 192].
* [cite_start]**Mô hình LLM:** Sử dụng `Gemini 1.5 Flash` (Free Tier) cho khả năng tư vấn và Tool-calling[cite: 193].
* [cite_start]**Hạ tầng Vector:** Sử dụng gói miễn phí của `Supabase` (Postgres + pgvector)[cite: 194].
* [cite_start]**Try-On GPU:** Tận dụng Gradio Client để gọi API miễn phí từ Hugging Face (mô hình IDM-VTON)[cite: 195].

---

## 5. Bảo mật & Quyền riêng tư (Privacy-First)

* [cite_start]**Ảnh người dùng:** Chỉ xử lý tại Client hoặc sử dụng **Presigned URL** với thời hạn < 1 giờ trên Cloudflare R2[cite: 197].
* [cite_start]**Tự động xóa:** Thiết lập chính sách Lifecycle tự động xóa ảnh gốc ngay sau khi xử lý Try-On xong[cite: 198].

---

## 6. Lộ trình triển khai (Roadmap)

* [cite_start]**Giai đoạn 1 (Tuần 1-2):** Thiết lập WebSocket Chat và RAG cơ bản với pgvector[cite: 200].
* [cite_start]**Giai đoạn 2 (Tuần 3-4):** Tích hợp nhận diện Body tại Client và module tư vấn Size[cite: 201].
* [cite_start]**Giai đoạn 3 (Tuần 5-6):** Triển khai Virtual Try-On bất đồng bộ qua Queue[cite: 202].
* [cite_start]**Giai đoạn 4 (Tuần 7-8):** Xây dựng Dashboard Admin AI với quy trình phê duyệt HITL[cite: 203].

> [cite_start]**Senior Tip:** Hãy nhấn mạnh khả năng thay đổi "bộ não" AI linh hoạt qua việc cấu hình lại Adapter làm minh chứng cho tư duy Clean Architecture[cite: 204].