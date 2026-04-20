# Test Plan — AI Fashion Ecosystem

## 1. Kiểm thử Domain & Business Logic (Core Layer)

### Mục tiêu

Đảm bảo nghiệp vụ độc lập với framework (Spring, DB, AI provider).

### Kịch bản

* Chạy **JUnit tests** cho `RecommendProductInteractor` mà KHÔNG khởi chạy Spring Context.
* Mock `AIModelPort`.

### Expected Behavior

* Khi LLM trả về intent `RECOMMEND`:

    * Hệ thống phải gọi đúng service tìm kiếm sản phẩm.
    * Không phụ thuộc vào implementation cụ thể của AI provider.

---

## 2. Kiểm thử Nhận diện Body (Split Inference)

### Mục tiêu

Xác minh dữ liệu ảnh KHÔNG được gửi lên server.

### Kịch bản

1. Mở DevTools → tab Network.
2. Thực hiện nhận diện body.

### Expected Behavior

* Request gửi lên backend chỉ chứa JSON:

```json
{
  "shoulder_width": 45.5,
  "chest": 92
}
```

* KHÔNG chứa:

    * Base64 image
    * Blob file
    * Multipart upload

---

## 3. Kiểm thử Admin AI & Human-in-the-Loop (HITL)

### Mục tiêu

Ngăn AI tự động thay đổi dữ liệu kinh doanh.

### Kịch bản

1. Gửi yêu cầu: "Lên chiến dịch giảm giá 20% cho áo thun".
2. Kiểm tra DB:

    * Bảng `pricing_drafts`

### Expected Behavior

* Record phải có trạng thái:

```
DRAFT
```

* Bảng `products` KHÔNG bị thay đổi.

### Approval Flow

* Chỉ khi Admin nhấn **Approve**:

    * Giá mới được cập nhật vào production DB.

---

## 4. Kiểm thử Virtual Try-On (Async Pipeline)

### Mục tiêu

Đảm bảo pipeline bất đồng bộ và bảo mật ảnh.

### Kịch bản

1. Gửi request try-on.
2. Đo thời gian response.

### Expected Behavior

* Response trả về ngay:

```json
{
  "taskId": "xyz",
  "status": "PENDING"
}
```

* Latency < 500ms

### Queue Validation

* Task phải xuất hiện trong Redis queue.

### Completion

* Nhận kết quả qua WebSocket.

### Privacy Check

* Ảnh gốc phải bị xóa trong < 5 phút khỏi storage.

---

## 5. Kiểm thử Tìm kiếm Vector (RAG)

### Mục tiêu

Đảm bảo hiệu năng và độ chính xác tìm kiếm.

### Kịch bản

* Query:

```
"Váy hoa nhẹ nhàng cho tiệc trà"
```

### Expected Behavior

* SQL query phải:

    1. Filter theo metadata trước (category, price)
    2. Sau đó mới thực hiện vector similarity search

---

## 6. Seeding Data cho pgvector

### Bước 1: Enable extension

```sql
CREATE EXTENSION IF NOT EXISTS vector;
```

### Bước 2: Insert sample data

```sql
INSERT INTO product_vectors (product_id, embedding, metadata)
VALUES
(
    gen_random_uuid(),
    '[0.012, -0.023, 0.045,...]',
    '{"category": "office-wear", "style": "thanh lịch", "color": "white", "price": 550000}'
),
(
    gen_random_uuid(),
    '[-0.051, 0.033, 0.011,...]',
    '{"category": "t-shirt", "style": "năng động", "color": "black", "price": 250000}'
);
```

### Bước 3: Tạo index HNSW

```sql
CREATE INDEX ON product_vectors USING hnsw (embedding vector_cosine_ops)
WITH (m = 16, ef_construction = 200);
```

### Ghi chú

* Vector hiện tại là dummy.
* Cần dùng Embedding API để tạo vector thật.

---

## 7. Checklist tổng

* [ ] Domain test không phụ thuộc Spring
* [ ] Không upload ảnh lên server
* [ ] Admin AI không tự cập nhật DB
* [ ] Try-on chạy async + có queue
* [ ] Ảnh user bị xóa đúng hạn
* [ ] Vector search có filter + similarity

---

## 8. Kết luận

Test plan này đảm bảo:

* Tính độc lập của kiến trúc
* Tính bảo mật dữ liệu
* Tính đúng đắn của AI workflow
* Tính hiệu năng của hệ thống
