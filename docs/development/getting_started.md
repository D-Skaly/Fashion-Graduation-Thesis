# Cài đặt & Khởi chạy (Getting Started)

> Mục lục tài liệu: [docs/README.md](../README.md) · Cấu hình chi tiết thêm: [dev_setup.md](dev_setup.md).

**Tác giả (Owner):** DSkaly | **Ngày cập nhật:** 2026-02-20
**Reviewer:** Principal Architect | **Trạng thái:** Active | **Version:** v1.1

Để chạy được dự án Fashion Graduation Thesis, bạn cần cài đặt các phần mềm sau:

## 1. Java Development Kit (JDK) 21

Dự án yêu cầu Java 21 (LTS) để chạy Spring Boot 3 và Virtual Threads.

1.  **Tải về:**
    - Truy cập: [Adoptium Eclipse Temurin 21](https://adoptium.net/temurin/releases/?version=21)
    - Chọn phiên bản cho Windows (x64), định dạng `.msi`.
2.  **Cài đặt:**
    - Chạy file `.msi` vừa tải.
    - ⚠️ **QUAN TRỌNG:** Ở màn hình chọn tính năng, hãy chắc chắn chọn **"Set JAVA_HOME variable"** (Biểu tượng ổ đĩa cứng sẽ chuyển sang màu xám không có dấu X đỏ). Điều này giúp Windows nhận diện lệnh `java`.
3.  **Kiểm tra:**
    - Mở Terminal (CMD hoặc PowerShell) mới.
    - Gõ: `java -version`
    - Nếu hiện `openjdk version "21..."` là thành công.

## 2. Docker Desktop (Khuyên dùng)

Docker giúp chạy Database (PostgreSQL) và Redis mà không cần cài đặt rườm rà.

1.  **Tải về:**
    - Truy cập: [Docker Desktop for Windows](https://www.docker.com/products/docker-desktop/)
2.  **Cài đặt:**
    - Chạy file tải về và Next liên tục (Giữ nguyên mặc định Use WSL 2).
    - Sau khi cài xong, bạn có thể cần Restart máy.
3.  **Kiểm tra:**
    - Mở Docker Desktop lên, đợi icon ở góc dưới bên trái chuyển màu xanh lá.
    - Gõ trong terminal: `docker -v`.

---

## Giải pháp thay thế (Nếu máy yếu không cài được Docker)

Nếu bạn không muốn dùng Docker, bạn phải cài PostgreSQL thủ công:

1.  **Tải PostgreSQL:** [Download PostgreSQL](https://www.postgresql.org/download/windows/)
2.  **Cài đặt:**
    - Đặt mật khẩu cho user `postgres` là `password` (để khớp với file cấu hình dự án).
    - Port mặc định là `5432`.
3.  **Tạo Database:**
    - Mở **pgAdmin** (cài cùng PostgreSQL).
    - Chuột phải `Databases` -> Create -> Database...
    - Đặt tên là `fashion_db`.
