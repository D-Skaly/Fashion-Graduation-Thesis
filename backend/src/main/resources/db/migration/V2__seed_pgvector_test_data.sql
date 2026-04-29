-- Seeding data for pgvector based on test_plan.md (aligned with V1 products columns)
-- Idempotent: only insert if products do not exist

CREATE EXTENSION IF NOT EXISTS vector;

INSERT INTO products (
    id,
    name,
    description,
    base_price,
    sku,
    slug,
    is_active,
    created_at,
    updated_at
)
SELECT
    '00000000-0000-0000-0000-000000000001'::uuid,
    'Váy hoa văn phòng',
    'Thanh lịch, phù hợp đi làm',
    550000,
    'SEED-SKU-PGVECTOR-001',
    'seed-vay-hoa-van-phong-001',
    true,
    now(),
    now()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE id = '00000000-0000-0000-0000-000000000001');

INSERT INTO products (
    id,
    name,
    description,
    base_price,
    sku,
    slug,
    is_active,
    created_at,
    updated_at
)
SELECT
    '00000000-0000-0000-0000-000000000002'::uuid,
    'Áo thun năng động',
    'Thoải mái, màu đen',
    250000,
    'SEED-SKU-PGVECTOR-002',
    'seed-ao-thun-nang-dong-002',
    true,
    now(),
    now()
WHERE NOT EXISTS (SELECT 1 FROM products WHERE id = '00000000-0000-0000-0000-000000000002');

-- 384-dimensional vectors (schema embedding_vector vector(384))
UPDATE products
SET embedding_vector = (concat('[0.012', repeat(',0', 383), ']'))::vector(384)
WHERE id = '00000000-0000-0000-0000-000000000001';

UPDATE products
SET embedding_vector = (concat('[-0.05', repeat(',0', 383), ']'))::vector(384)
WHERE id = '00000000-0000-0000-0000-000000000002';
