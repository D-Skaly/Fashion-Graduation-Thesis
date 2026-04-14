# Hướng dẫn Triển khai Production

## Yêu cầu hệ thống

- Docker 20.10+
- Docker Compose 2.0+
- 4GB RAM (khuyến nghị 8GB)
- 20GB disk space

## Các bước triển khai

### 1. Chuẩn bị môi trường

```bash
# Copy file môi trường
cp .env.prod.template .env.prod

# Chỉnh sửa các biến môi trường (dùng editor của bạn)
nano .env.prod
```

### 2. Tạo SSL Certificate (self-signed hoặc Let's Encrypt)

**Option A: Self-signed (development/testing)**
```bash
mkdir -p nginx/ssl
openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
  -keyout nginx/ssl/key.pem \
  -out nginx/ssl/cert.pem \
  -subj "/C=VN/ST=HCMC/L=HCMC/O=Fashion/CN=fashion.skaly.com"
```

**Option B: Let's Encrypt (production)**
```bash
# Cài certbot
certbot certonly --standalone -d fashion.skaly.com

# Copy certificates
cp /etc/letsencrypt/live/fashion.skaly.com/fullchain.pem nginx/ssl/cert.pem
cp /etc/letsencrypt/live/fashion.skaly.com/privkey.pem nginx/ssl/key.pem
```

### 3. Build và khởi động

```bash
# Build images
docker-compose -f docker-compose.prod.yml build

# Khởi động
docker-compose -f docker-compose.prod.yml up -d

# Kiểm tra logs
docker-compose -f docker-compose.prod.yml logs -f
```

### 4. Kiểm tra hệ thống

```bash
# Health check
curl http://localhost/health

# API health
curl http://localhost/api/v1/health
```

### 5. Backup & Restore

**Backup database:**
```bash
docker exec fashion_postgres pg_dump -U fashion_user fashion_db > backup_$(date +%Y%m%d).sql
```

**Restore database:**
```bash
docker exec -i fashion_postgres psql -U fashion_user fashion_db < backup_20240101.sql
```

## Lệnh hữu ích

```bash
# Xem logs
docker-compose -f docker-compose.prod.yml logs -f [service]

# Restart service
docker-compose -f docker-compose.prod.yml restart [service]

# Update deployment
docker-compose -f docker-compose.prod.yml pull
docker-compose -f docker-compose.prod.yml up -d

# Stop all
docker-compose -f docker-compose.prod.yml down

# Stop và xóa volumes (CẨN THẬN)
docker-compose -f docker-compose.prod.yml down -v
```

## Troubleshooting

| Vấn đề | Cách xử lý |
|--------|-----------|
| Port đã được sử dụng | `netstat -tlnp` tìm và kill process |
| Permission denied | `chmod 644 nginx/ssl/*.pem` |
| Database không kết nối | Kiểm tra `DATABASE_URL` và firewall |
| 502 Bad Gateway | Kiểm tra backend container health |
