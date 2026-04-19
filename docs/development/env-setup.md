# Hướng dẫn Cấu hình Environment (Dev)

## Các file cần tạo

### 1. Backend: `backend/.env`

Copy từ file `.env.dev` có sẵn:

```bash
cd backend
cp .env.dev .env
```

**Chỉnh sửa các giá trị bắt buộc:**

| Biến | Giá trị | Mô tả |
|---|---|---|
| `JWT_SECRET_KEY` | `openssl rand -base64 32` | Bắt buộc - secret key cho JWT |
| `DATABASE_PASSWORD` | `password` | Mật khẩu PostgreSQL |

**Các biến tùy chọn:**

| Biến | Khi nào cần |
|---|---|
| `GOOGLE_CLIENT_ID` / `GOOGLE_CLIENT_SECRET` | Khi dùng đăng nhập Google OAuth |
| `GEMINI_API_KEY` | Khi bật tính năng AI |
| `AI_ENABLED` | Đặt `true` để bật AI |
| `MINIO_*` | Khi dùng MinIO cho file storage |

### 2. Frontend: `frontend/.env.local`

Tạo file mới:

```bash
cd frontend
 touch .env.local
```

Nội dung tối thiểu:

```env
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080/api/v1
```

### 3. Tùy chọn: AI Orchestrator

Nếu chạy AI Orchestrator, tạo `ai-orchestrator/.env`:

```env
PORT=3001
```

## Bước chạy Dev

```bash
# 1. Khởi động PostgreSQL + Redis
docker-compose up -d

# 2. Chạy backend
cd backend
./mvnw spring-boot:run

# 3. Chạy frontend (terminal khác)
cd frontend
npm run dev
```

## Kiểm tra

- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api/v1
- API Health: http://localhost:8080/actuator/health
