# Supabase + pgvector — bảng `products` và tìm kiếm tương đồng (RAG)

Tài liệu ngắn cho Giai đoạn 1: lưu embedding sản phẩm trên Supabase (Postgres + pgvector) và truy vấn nearest neighbor.

## 1. Bật extension

Trong SQL Editor của Supabase:

```sql
create extension if not exists vector;
```

## 2. Bảng `products` với cột embedding

Ví dụ vector 384 chiều (khớp `spring.ai.vectorstore.pgvector.dimensions` trong backend). Đổi `384` nếu bạn dùng mô hình embedding khác.

```sql
create table if not exists public.products (
  id uuid primary key default gen_random_uuid(),
  name text not null,
  description text,
  base_price numeric(12,2),
  -- Vector embedding (pgvector). Kích thước phải khớp mô hình embed.
  embedding vector(384),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

-- Index HNSW cho cosine similarity (tương đương khoảng cách góc)
create index if not exists products_embedding_hnsw
  on public.products
  using hnsw (embedding vector_cosine_ops);
```

## 3. Câu lệnh tìm sản phẩm gần nhất (tương đồng ngữ nghĩa)

Giả sử bạn đã có vector truy vấn `$1::vector` (embedding của câu người dùng), trả về top 5:

```sql
select
  id,
  name,
  description,
  base_price,
  1 - (embedding <=> $1::vector) as similarity_score
from public.products
where embedding is not null
order by embedding <=> $1::vector
limit 5;
```

Toán tử `<=>` là **cosine distance** trong pgvector. `1 - distance` là điểm tương đồng thô (càng gần 1 càng giống).

## 4. Ghi chú tích hợp Spring Boot

- Backend hiện dùng JPA/Flyway trên Postgres local; Supabase chỉ là một Postgres có pgvector — cùng mô hình dữ liệu (cột `embedding`) là có thể dùng chung pipeline RAG.
- Khi import dữ liệu từ Supabase sang môi trường dev, đảm bảo **cùng số chiều** embedding giữa bước index (`vector(384)`) và service `ProductEmbeddingService`.
