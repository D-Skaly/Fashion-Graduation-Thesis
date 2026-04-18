-- FI-Agent Module 1 & 2 core data model
-- Step 1: database architecture for AI Stylist + AI Strategist

-- Product style vector (pgvector) for stylist-specific similarity search
ALTER TABLE products
    ADD COLUMN IF NOT EXISTS style_vector vector(384);

CREATE INDEX IF NOT EXISTS idx_products_style_vector_hnsw
    ON products
    USING hnsw (style_vector vector_cosine_ops);

-- Customer-facing personalization profile (Module 1)
CREATE TABLE IF NOT EXISTS customer_profiles (
    id uuid PRIMARY KEY,
    customer_id uuid NOT NULL UNIQUE,
    preferred_style varchar(100),
    budget_min double precision,
    budget_max double precision,
    w_fit double precision NOT NULL DEFAULT 1.0,
    style_preference_vector vector(384),
    created_at timestamp(6) without time zone NOT NULL DEFAULT now(),
    updated_at timestamp(6) without time zone NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_customer_profiles_style_vector_hnsw
    ON customer_profiles
    USING hnsw (style_preference_vector vector_cosine_ops);

-- Business analytics signals used by Module 2 strategist and scoring engine
CREATE TABLE IF NOT EXISTS analytics (
    id uuid PRIMARY KEY,
    product_id uuid NOT NULL REFERENCES products(id),
    analytics_date date NOT NULL,
    conversion_rate double precision NOT NULL DEFAULT 0.0,
    gap_analysis_score double precision NOT NULL DEFAULT 0.0,
    finance_multiplier double precision NOT NULL DEFAULT 1.0,
    business_score double precision NOT NULL DEFAULT 0.0,
    market_score double precision NOT NULL DEFAULT 0.0,
    created_at timestamp(6) without time zone NOT NULL DEFAULT now(),
    updated_at timestamp(6) without time zone NOT NULL DEFAULT now(),
    CONSTRAINT uk_analytics_product_date UNIQUE (product_id, analytics_date)
);

CREATE INDEX IF NOT EXISTS idx_analytics_product_date
    ON analytics (product_id, analytics_date DESC);
