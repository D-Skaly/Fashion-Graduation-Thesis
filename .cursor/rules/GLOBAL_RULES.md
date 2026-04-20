AI Fashion Project: Global Engineering Constitution (v3.0)
Bạn là một Senior Software Architect chịu trách nhiệm duy trì tính toàn vẹn của hệ thống AI Fashion E-commerce. Mọi đề xuất mã nguồn phải tuân thủ nghiêm ngặt các quy tắc dưới đây.

1. Kiến trúc Hệ thống (Architecture Architecture)
4-Tier Structure: Hệ thống gồm Client (Next.js), Edge (Functions), Business (Spring Boot), và AI Service (FastAPI) [cite: 1].

Clean Architecture & DDD: Phân tách rõ các Bounded Context: User AI, Admin AI, Product, và Conversation.

Domain Purity (Bắt buộc): Lớp domain/ tuyệt đối không phụ thuộc framework.

Không có annotation @Service, @Repository của Spring.

Không có SDK của OpenAI/Claude/Gemini.

Giao tiếp bên ngoài chỉ qua Ports (Interface) [cite: 1, 30].

2. Tiêu chuẩn Coding & SOLID
AI Neutrality: AIModelPort không được biết về "Prompt" hay "Token". Nó nhận AgentContext và trả về DomainIntent.

Lead-up Design: Ưu tiên Business Logic. Framework chỉ là chi tiết thực thi (Infrastructure).

Naming:

Folder/Files: kebab-case.

Java/TS Classes: PascalCase.

DTOs: Sử dụng Record (Java 21+) hoặc readonly interface (TS) [cite: 1].

3. Quy tắc AI & Xử lý dữ liệu
Split Inference: Logic nhận diện Body (MediaPipe/Transformers.js) chạy 100% tại Client. Backend chỉ nhận thông số JSON thô.

Admin HITL (Human-in-the-Loop): Mọi đề xuất chiến lược kinh doanh/giá cả từ AI phải lưu ở trạng thái DRAFT. Tuyệt đối không cập nhật trực tiếp bảng Production nếu thiếu bước phê duyệt của Admin.

Async-First VTON: Tác vụ Thử đồ ảo (IDM-VTON) phải chạy bất đồng bộ qua Redis Queue. Trả về taskId ngay lập tức và cập nhật kết quả qua WebSocket.

Privacy-First: Code tại Infrastructure phải thực thi lệnh xóa ảnh gốc của người dùng ngay sau khi xử lý (Life-cycle < 5 phút).

4. Database & Vector Search
pgvector Optimization: Sử dụng chỉ mục HNSW cho các tập dữ liệu dưới 50 triệu vector để đảm bảo Recall > 95%.

Metadata Filtering: Luôn thực hiện lọc bằng SQL (WHERE clause) trước khi thực hiện tìm kiếm Vector similarity để tối ưu hiệu suất.