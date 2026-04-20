Global Development & Refactoring Workflow
Khi thực hiện bất kỳ nhiệm vụ nào (viết mới hoặc chỉnh sửa module cũ), AI Agent phải đi theo luồng 5 bước "Core-Out" sau đây:

Bước 1: Thanh tra Kiến trúc (Architecture Audit)
Kiểm tra mã nguồn hiện tại đối chiếu với GLOBAL_RULES.md.

Tìm các vi phạm: Logic framework nằm trong Domain, xử lý ảnh đồng bộ, thiếu chốt chặn HITL cho Admin.

Output: Danh sách checklist các lỗi kiến trúc cần khắc phục trước khi code.

Bước 2: Lập kế hoạch (Interface First)
Phác thảo chiến lược sửa đổi.

Bắt buộc: Định nghĩa các Interface cho Input Port (UseCase) và Output Port (Repository/AI Service) trước khi viết logic thực thi.

Chờ User phản hồi /approve mới chuyển sang bước tiếp theo.

Bước 3: Triển khai Lõi (Domain & Application)
Cập nhật các Entities và Value Objects tại domain/.

Viết Interactor tại application/ để điều phối logic nghiệp vụ.

Đảm bảo logic "Tư vấn Size" hoặc "Lập kế hoạch" chỉ sử dụng dữ liệu từ Port, không gọi API trực tiếp.

Bước 4: Triển khai Adapter (Infrastructure)
Thực thi AIModelPort bằng GeminiAIAdapter hoặc ClaudeAIAdapter.

Cấu hình pgvector repository với metadata filtering.

Triển khai Worker gọi IDM-VTON bất đồng bộ nếu liên quan đến module Thử đồ.

Bước 5: Kiểm tra & Xác minh (Verification)
Viết Unit Test cho Domain (không khởi động Spring Context).

Kiểm tra luồng dữ liệu: Xác nhận ảnh không gửi lên server ở tính năng nhận diện body.

Kiểm tra trạng thái DRAFT cho các đề xuất của Admin AI.

Handover: Giải thích cách code mới tuân thủ nguyên lý SOLID và AI Neutrality.