-- Seeding data for pgvector based on test_plan.md

-- Step 1: Enable extension (already in V1, but good to ensure)
CREATE EXTENSION IF NOT EXISTS vector;

-- Step 2: Insert sample data with dummy vectors
-- Note: In production, embeddings should be generated via an Embedding API
INSERT INTO products (id, name, description, base_price, is_active, created_at, updated_at)
VALUES
('00000000-0000-0000-0000-000000000001', 'Váy hoa văn phòng', 'Thanh lịch, phù hợp đi làm', 550000, true, now(), now()),
('00000000-0000-0000-0000-000000000002', 'Áo thun năng động', 'Thoải mái, màu đen', 250000, true, now(), now());

-- Update embeddings (pgvector)
UPDATE products SET embedding_vector = '[0.012, -0.023, 0.045, 0.01, 0.02, 0.03, 0.04, 0.05, 0.06, 0.07, 0.08, 0.09, 0.1, 0.11, 0.12, 0.13]' 
WHERE id = '00000000-0000-0000-0000-000000000001';

UPDATE products SET embedding_vector = '[-0.051, 0.033, 0.011, -0.01, -0.02, -0.03, -0.04, -0.05, -0.06, -0.07, -0.08, -0.09, -0.1, -0.11, -0.12, -0.13]'
WHERE id = '00000000-0000-0000-0000-000000000002';

-- Step 3: Tạo index HNSW (if not exists)
-- CREATE INDEX ON products USING hnsw (embedding_vector vector_cosine_ops);
