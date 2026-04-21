# Development Setup Guide

> Mục lục: [docs/README.md](../README.md) · Hướng dẫn cài JDK/Docker từng bước: [getting_started.md](getting_started.md).

## Khởi động Development Environment

### 1. Chuẩn bị Database

```bash
# Tạo database dev
cd backend
docker-compose up -d postgres redis

# Hoặc tạo database thủ công
psql -U postgres -c "CREATE DATABASE fashion_db_dev;"
```

### 2. Cấu hình môi trường

```bash
cp backend/.env.dev.template backend/.env.dev
# Chỉnh sửa file .env.dev nếu cần
```

### 3. Chạy Backend (Dev Profile)

```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Hoặc với env file
export $(cat .env.dev | xargs) && ./mvnw spring-boot:run
```

### 4. Chạy Frontend

```bash
cd frontend
npm install
npm run dev
```

### 5. Chạy MinIO (cho image upload)

```bash
# Thêm vào docker-compose.override.yml hoặc chạy riêng
docker run -p 9000:9000 -p 9001:9001 \
  --name minio \
  -e "MINIO_ROOT_USER=minioadmin" \
  -e "MINIO_ROOT_PASSWORD=minioadmin" \
  minio/minio server /data --console-address ":9001"
```

## Switch Profiles

### Development (default)
```bash
./mvnw spring-boot:run
# hoặc
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

### Production
```bash
SPRING_PROFILES_ACTIVE=prod ./mvnw spring-boot:run
```

## Debug Configuration

### Dev Profile đã bật:
- ✅ SQL logging (DEBUG)
- ✅ Hibernate parameter binding (TRACE)
- ✅ Spring Security debug logs
- ✅ All actuator endpoints exposed
- ✅ Detailed error messages
- ✅ CORS cho localhost:3000

### IDE Debug
- Port: 8080
- Actuator: http://localhost:8080/actuator
- API: http://localhost:8080/api/v1

## Hot Reload

### Backend
```bash
./mvnw spring-boot:run -Dspring-boot.run.fork=false
```

### Frontend
```bash
npm run dev  # Next.js hot reload tự động
```
