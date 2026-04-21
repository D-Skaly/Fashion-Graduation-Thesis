# Documentation Policy & Lifecycle

**Tác giả (Owner):** DSkaly | **Ngày cập nhật:** 2026-02-20
**Reviewer:** Technical Lead | **Trạng thái:** Active | **Version:** v1.0

## 1. Mục Đích (Purpose)

Xác định quy trình vòng đời của tài liệu (Documentation Lifecycle) và trách nhiệm của thành viên trong việc bảo trì tài liệu tại Fashion E-Commerce.

**Điểm vào tài liệu:** mọi thay đổi lớn nên phản ánh trong [docs/README.md](../README.md) (mục lục, bảng “đọc trước”, liên kết tới AGENTS.md).

## 2. Vòng Đời Tài Liệu (Lifecycle)

Mọi tài liệu kỹ thuật trong thư mục `/docs` phải tuân theo 4 trạng thái:

1. **Draft:** Đang soản thảo (WIP), chưa nên áp dụng vào code thực tế.
2. **Review:** Đang chờ Leader hoặc Owner duyệt qua Pull Request.
3. **Active:** Đã chốt và trở thành tiêu chuẩn bắt buộc (Single Source of Truth).
4. **Deprecated:** Lỗi thời, được giữ lại để tham khảo do hệ thống đã nâng cấp (sẽ tự động move vào thư mục `/docs/archive` sau 6 tháng).

## 3. Ownership & Trách Nhiệm

- **Thư mục `/architecture`:** Quản lý bởi Software Architect. Bất kỳ thay đổi (ADR) nào phải được Architect approve.
- **Thư mục `/domains`:** Quản lý bởi Domain Tech Lead (VD: Team Checkout lo `/domains/cart-domain.md`).
- **Thư mục `/api`:** Tự động sinh từ Swagger/OpenAPI. Lập trình viên có trách nhiệm viết Javadoc/Annotation đúng chuẩn. Không viết tay API docs.

## 4. Quy Tắc Phiên Bản (Versioning Policy)

- Bất kỳ thay đổi nào làm phá vỡ cấu trúc (Breaking change) phải nâng Major version của tài liệu (v1.0 -> v2.0).
- Cập nhật thêm tính năng nâng Minor version (v1.0 -> v1.1).

## 5. Changelog

- **v1.0:** Khởi tạo chính sách tài liệu chuẩn Enterprise (2026-02-20)
