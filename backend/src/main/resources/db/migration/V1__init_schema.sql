-- ============================================
-- Fashion E-Commerce - Complete Database Schema (Consolidated V1)
-- ============================================
-- This migration consolidates all previous migrations (V1-V14)
-- into a single comprehensive schema.

-- ============================================
-- Extensions
-- ============================================
CREATE EXTENSION IF NOT EXISTS vector;

-- ============================================
-- Core Tables with All Columns
-- ============================================

-- Users Table
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    avatar_url VARCHAR(255),
    phone VARCHAR(255),
    provider VARCHAR(255) CHECK (provider IN ('LOCAL', 'GOOGLE')),
    role VARCHAR(255) NOT NULL CHECK (role IN ('USER', 'ADMIN')),
    created_at TIMESTAMP(6) WITHOUT TIME ZONE,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE,
    last_login_at TIMESTAMP(6) WITHOUT TIME ZONE,
    deleted_at TIMESTAMP,
    deleted_by UUID
);

-- Categories Table
CREATE TABLE categories (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    parent_id UUID REFERENCES categories(id),
    deleted_at TIMESTAMP,
    deleted_by UUID
);

-- Products Table
CREATE TABLE products (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    base_price NUMERIC(38,2) NOT NULL CHECK (base_price >= 0),
    discount_price NUMERIC(38,2) CHECK (discount_price >= 0 AND (discount_price <= base_price OR discount_price IS NULL)),
    sku VARCHAR(255) NOT NULL UNIQUE,
    slug VARCHAR(255) NOT NULL UNIQUE,
    brand VARCHAR(255),
    tags VARCHAR(255)[],
    category_id UUID REFERENCES categories(id),
    stock INTEGER NOT NULL DEFAULT 0 CHECK (stock >= 0),
    view_count INTEGER DEFAULT 0,
    sold_count INTEGER DEFAULT 0,
    rating_avg NUMERIC(3,2) DEFAULT 0,
    is_active BOOLEAN DEFAULT true,
    is_featured BOOLEAN DEFAULT false,
    care_instructions TEXT,
    material VARCHAR(255),
    weight DOUBLE PRECISION,
    dimensions VARCHAR(255),
    meta_title VARCHAR(255),
    meta_description TEXT,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE,
    deleted_at TIMESTAMP,
    deleted_by UUID,
    embedding_vector vector(384),
    style_vector vector(384)
);

-- ProductEntity Images Table
CREATE TABLE product_images (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    url VARCHAR(255) NOT NULL,
    alt VARCHAR(255),
    is_primary BOOLEAN DEFAULT false,
    sort_order INTEGER DEFAULT 0,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE
);

-- ProductEntity Tags Table
CREATE TABLE product_tags (
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    tag VARCHAR(255) NOT NULL,
    PRIMARY KEY (product_id, tag)
);

-- Orders Table
CREATE TABLE orders (
    id UUID PRIMARY KEY,
    order_number VARCHAR(255) NOT NULL UNIQUE,
    user_id UUID NOT NULL REFERENCES users(id),
    sub_total NUMERIC(38,2) NOT NULL DEFAULT 0,
    tax_amount NUMERIC(38,2) DEFAULT 0,
    shipping_cost NUMERIC(38,2) DEFAULT 0,
    discount_amount NUMERIC(38,2) DEFAULT 0,
    discount_code VARCHAR(50),
    total_amount NUMERIC(38,2) NOT NULL DEFAULT 0,
    shipping_address TEXT,
    notes TEXT,
    cancelled_at TIMESTAMP(6) WITHOUT TIME ZONE,
    cancelled_reason VARCHAR(500),
    status VARCHAR(255) NOT NULL CHECK (status IN ('PENDING', 'CONFIRMED', 'SHIPPED', 'COMPLETED', 'CANCELLED', 'REFUNDED')),
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE,
    deleted_at TIMESTAMP,
    deleted_by UUID
);

-- ShippingEntity Table
CREATE TABLE shipping (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL UNIQUE REFERENCES orders(id) ON DELETE CASCADE,
    carrier VARCHAR(100),
    tracking_number VARCHAR(100),
    shipping_method VARCHAR(50),
    shipping_cost NUMERIC(38,2) DEFAULT 0,
    estimated_delivery TIMESTAMP(6) WITHOUT TIME ZONE,
    shipped_at TIMESTAMP(6) WITHOUT TIME ZONE,
    delivered_at TIMESTAMP(6) WITHOUT TIME ZONE,
    shipping_address TEXT NOT NULL,
    recipient_name VARCHAR(100),
    recipient_phone VARCHAR(20),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP(6) WITHOUT TIME ZONE,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE
);

-- OrderEntity Notes Table
CREATE TABLE order_notes (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    user_id UUID REFERENCES users(id),
    content TEXT NOT NULL,
    is_internal BOOLEAN DEFAULT true,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE
);

-- OrderEntity Status History Table
CREATE TABLE order_status_history (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    status VARCHAR(255) NOT NULL,
    note VARCHAR(500),
    created_at TIMESTAMP(6) WITHOUT TIME ZONE
);

-- Payments Table (Base)
CREATE TABLE payments (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL UNIQUE REFERENCES orders(id) ON DELETE CASCADE,
    transaction_id VARCHAR(255) UNIQUE,
    payment_method VARCHAR(255) NOT NULL,
    payment_status VARCHAR(255) NOT NULL,
    amount NUMERIC(38,2) NOT NULL,
    currency VARCHAR(255) NOT NULL DEFAULT 'VND',
    paid_at TIMESTAMP(6) WITHOUT TIME ZONE,
    failure_reason VARCHAR(255),
    gateway_response TEXT,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE
);

-- VNPay Payments Table
CREATE TABLE vnpay_payments (
    id UUID PRIMARY KEY REFERENCES payments(id) ON DELETE CASCADE,
    vnp_txn_ref VARCHAR(255),
    vnp_transaction_no VARCHAR(255),
    vnp_response_code VARCHAR(255),
    vnp_bank_code VARCHAR(255),
    vnp_pay_date VARCHAR(255)
);

-- MoMo Payments Table
CREATE TABLE momo_payments (
    id UUID PRIMARY KEY REFERENCES payments(id) ON DELETE CASCADE,
    momo_order_id VARCHAR(255),
    momo_request_id VARCHAR(255),
    momo_transaction_id VARCHAR(255),
    momo_result_code INTEGER,
    momo_message VARCHAR(255)
);

-- ProductEntity Variants Table
CREATE TABLE product_variants (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL REFERENCES products(id),
    size VARCHAR(255) NOT NULL,
    color VARCHAR(255) NOT NULL,
    stock_quantity INTEGER NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
    price_adjustment NUMERIC(38,2) DEFAULT 0,
    sku_code VARCHAR(255) UNIQUE
);

-- Carts Table
CREATE TABLE carts (
    id UUID PRIMARY KEY,
    user_id UUID UNIQUE REFERENCES users(id),
    guest_id VARCHAR(255) UNIQUE,
    coupon_code VARCHAR(255),
    discount_amount NUMERIC(38,2),
    created_at TIMESTAMP(6) WITHOUT TIME ZONE,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE
);

-- Cart Items Table
CREATE TABLE cart_items (
    id UUID PRIMARY KEY,
    cart_id UUID NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
    product_variant_id UUID NOT NULL REFERENCES product_variants(id),
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    snapshot_price NUMERIC(38,2) NOT NULL,
    added_at TIMESTAMP(6) WITHOUT TIME ZONE
);

-- OrderEntity Items Table
CREATE TABLE order_items (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_variant_id UUID NOT NULL REFERENCES product_variants(id),
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    snapshot_price NUMERIC(38,2) NOT NULL
);

-- Coupons Table
CREATE TABLE coupons (
    id UUID PRIMARY KEY,
    code VARCHAR(255) NOT NULL UNIQUE,
    discount_type VARCHAR(255) NOT NULL CHECK (discount_type IN ('PERCENTAGE', 'FIXED_AMOUNT')),
    discount_value NUMERIC(38,2) NOT NULL CHECK (discount_value > 0),
    min_order_value NUMERIC(38,2),
    max_discount_value NUMERIC(38,2),
    valid_from TIMESTAMP(6) WITHOUT TIME ZONE,
    valid_until TIMESTAMP(6) WITHOUT TIME ZONE,
    is_active BOOLEAN NOT NULL DEFAULT true,
    usage_limit INTEGER,
    used_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE,
    deleted_at TIMESTAMP,
    deleted_by UUID
);

-- Outbox Events Table
CREATE TABLE outbox_events (
    id UUID PRIMARY KEY,
    aggregate_type VARCHAR(255) NOT NULL,
    aggregate_id UUID NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    payload TEXT NOT NULL,
    status VARCHAR(255) NOT NULL CHECK (status IN ('PENDING', 'SENT', 'FAILED')),
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL
);

-- Event Publication Table (Spring Modulith)
CREATE TABLE event_publication (
    id UUID PRIMARY KEY,
    listener_id VARCHAR(255),
    event_type VARCHAR(255),
    serialized_event VARCHAR(255),
    publication_date TIMESTAMP(6) WITH TIME ZONE,
    completion_date TIMESTAMP(6) WITH TIME ZONE,
    completion_attempts INTEGER,
    status VARCHAR(255) CHECK (status IN ('PUBLISHED', 'PROCESSING', 'COMPLETED', 'FAILED', 'RESUBMITTED')),
    last_resubmission_date TIMESTAMP(6) WITH TIME ZONE
);

-- Reviews Table
CREATE TABLE reviews (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL REFERENCES products(id),
    user_id UUID NOT NULL REFERENCES users(id),
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    is_verified_purchase BOOLEAN DEFAULT false,
    helpful_count INTEGER DEFAULT 0,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE,
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE,
    deleted_at TIMESTAMP,
    deleted_by UUID
);

-- Wishlists Table
CREATE TABLE wishlists (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    product_id UUID NOT NULL REFERENCES products(id),
    created_at TIMESTAMP(6) WITHOUT TIME ZONE DEFAULT NOW(),
    UNIQUE(user_id, product_id)
);

-- FI-Agent Module Tables

-- Customer Profiles Table
CREATE TABLE customer_profiles (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL UNIQUE REFERENCES users(id),
    preferred_style VARCHAR(100),
    budget_min DOUBLE PRECISION,
    budget_max DOUBLE PRECISION,
    w_fit DOUBLE PRECISION NOT NULL DEFAULT 1.0,
    style_preference_vector vector(384),
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

-- Analytics Table
CREATE TABLE analytics (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL REFERENCES products(id),
    analytics_date DATE NOT NULL,
    conversion_rate DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    gap_analysis_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    finance_multiplier DOUBLE PRECISION NOT NULL DEFAULT 1.0,
    business_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    market_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_analytics_product_date UNIQUE (product_id, analytics_date)
);

-- AI Try-On Module Table
CREATE TABLE try_on_jobs (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    product_id UUID NOT NULL,
    user_image_url VARCHAR(255),
    result_image_url VARCHAR(255),
    status VARCHAR(50) CHECK (status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED')),
    error_message TEXT,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE DEFAULT NOW()
);

-- ============================================
-- Indexes
-- ============================================

-- User indexes
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_user_status_created ON orders(user_id, status, created_at DESC);
CREATE INDEX idx_orders_status_created_total ON orders(status, created_at DESC, total_amount);

-- ProductEntity indexes
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_category_active_featured_created 
    ON products(category_id, is_active, is_featured, created_at DESC);
CREATE INDEX idx_products_brand_active ON products(brand, is_active) WHERE is_active = true;
CREATE INDEX idx_products_tags ON products USING GIN(tags) WHERE is_active = true;
CREATE INDEX idx_products_embedding ON products USING hnsw (embedding_vector vector_cosine_ops);
CREATE INDEX idx_products_style_vector ON products USING hnsw (style_vector vector_cosine_ops);
CREATE INDEX idx_products_deleted_at ON products(deleted_at) WHERE deleted_at IS NOT NULL;
CREATE INDEX idx_products_featured_only ON products(created_at DESC) 
    WHERE is_featured = true AND is_active = true;
CREATE INDEX idx_products_new_arrivals ON products(created_at DESC) 
    WHERE is_active = true;

-- ProductEntity variants indexes
CREATE INDEX idx_product_variants_product ON product_variants(product_id);
CREATE INDEX idx_product_variants_sku ON product_variants(sku_code);

-- Cart indexes
CREATE INDEX idx_carts_user ON carts(user_id);
CREATE INDEX idx_carts_guest ON carts(guest_id);
CREATE INDEX idx_cart_items_cart ON cart_items(cart_id);
CREATE INDEX idx_cart_items_variant ON cart_items(product_variant_id);

-- OrderEntity items indexes
CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_order_items_variant ON order_items(product_variant_id);

-- Reviews indexes
CREATE INDEX idx_reviews_product ON reviews(product_id);
CREATE INDEX idx_reviews_user ON reviews(user_id);
CREATE INDEX idx_reviews_product_rating ON reviews(product_id, rating, created_at DESC);

-- Wishlist indexes
CREATE INDEX idx_wishlists_user ON wishlists(user_id);
CREATE INDEX idx_wishlists_product ON wishlists(product_id);

-- FI-Agent indexes
CREATE INDEX idx_customer_profiles_style_vector ON customer_profiles 
    USING hnsw (style_preference_vector vector_cosine_ops);
CREATE INDEX idx_analytics_product_date ON analytics (product_id, analytics_date DESC);

-- Soft delete indexes
CREATE INDEX idx_orders_deleted_at ON orders(deleted_at) WHERE deleted_at IS NOT NULL;
CREATE INDEX idx_users_deleted_at ON users(deleted_at) WHERE deleted_at IS NOT NULL;
CREATE INDEX idx_categories_deleted_at ON categories(deleted_at) WHERE deleted_at IS NOT NULL;
CREATE INDEX idx_reviews_deleted_at ON reviews(deleted_at) WHERE deleted_at IS NOT NULL;
CREATE INDEX idx_coupons_deleted_at ON coupons(deleted_at) WHERE deleted_at IS NOT NULL;

-- ============================================
-- Sample Data
-- ============================================

-- Seed Categories
INSERT INTO categories (id, name, slug, description) VALUES
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Men', 'men', 'Premium collection for men'),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'Women', 'women', 'Elegant collection for women'),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'Accessories', 'accessories', 'Luxury fashion accessories')
ON CONFLICT (slug) DO NOTHING;

-- Analyze all tables
ANALYZE product_images;
ANALYZE product_tags;
ANALYZE shipping;
ANALYZE order_notes;
ANALYZE order_status_history;
ANALYZE payments;
ANALYZE vnpay_payments;
ANALYZE momo_payments;
ANALYZE users;
ANALYZE categories;
ANALYZE products;
ANALYZE product_variants;
ANALYZE carts;
ANALYZE cart_items;
ANALYZE orders;
ANALYZE order_items;
ANALYZE coupons;
ANALYZE reviews;
ANALYZE wishlists;
ANALYZE outbox_events;
ANALYZE event_publication;
ANALYZE customer_profiles;
ANALYZE analytics;
