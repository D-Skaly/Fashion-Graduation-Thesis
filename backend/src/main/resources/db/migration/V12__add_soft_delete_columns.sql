-- Add soft delete columns to main tables
ALTER TABLE products ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE products ADD COLUMN IF NOT EXISTS deleted_by UUID;

ALTER TABLE orders ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS deleted_by UUID;

ALTER TABLE payments ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE payments ADD COLUMN IF NOT EXISTS deleted_by UUID;

ALTER TABLE users ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE users ADD COLUMN IF NOT EXISTS deleted_by UUID;

ALTER TABLE categories ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE categories ADD COLUMN IF NOT EXISTS deleted_by UUID;

ALTER TABLE reviews ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE reviews ADD COLUMN IF NOT EXISTS deleted_by UUID;

ALTER TABLE coupons ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;
ALTER TABLE coupons ADD COLUMN IF NOT EXISTS deleted_by UUID;

-- Create indexes on deleted_at for performance
CREATE INDEX idx_products_deleted_at ON products(deleted_at) WHERE deleted_at IS NOT NULL;
CREATE INDEX idx_orders_deleted_at ON orders(deleted_at) WHERE deleted_at IS NOT NULL;
CREATE INDEX idx_payments_deleted_at ON payments(deleted_at) WHERE deleted_at IS NOT NULL;
CREATE INDEX idx_users_deleted_at ON users(deleted_at) WHERE deleted_at IS NOT NULL;
CREATE INDEX idx_categories_deleted_at ON categories(deleted_at) WHERE deleted_at IS NOT NULL;
CREATE INDEX idx_reviews_deleted_at ON reviews(deleted_at) WHERE deleted_at IS NOT NULL;
CREATE INDEX idx_coupons_deleted_at ON coupons(deleted_at) WHERE deleted_at IS NOT NULL;

-- Create foreign key constraints for deleted_by (references users table)
ALTER TABLE products ADD CONSTRAINT fk_products_deleted_by 
    FOREIGN KEY (deleted_by) REFERENCES users(id) ON DELETE SET NULL;

ALTER TABLE orders ADD CONSTRAINT fk_orders_deleted_by 
    FOREIGN KEY (deleted_by) REFERENCES users(id) ON DELETE SET NULL;

ALTER TABLE payments ADD CONSTRAINT fk_payments_deleted_by 
    FOREIGN KEY (deleted_by) REFERENCES users(id) ON DELETE SET NULL;

ALTER TABLE categories ADD CONSTRAINT fk_categories_deleted_by 
    FOREIGN KEY (deleted_by) REFERENCES users(id) ON DELETE SET NULL;

ALTER TABLE reviews ADD CONSTRAINT fk_reviews_deleted_by 
    FOREIGN KEY (deleted_by) REFERENCES users(id) ON DELETE SET NULL;

ALTER TABLE coupons ADD CONSTRAINT fk_coupons_deleted_by 
    FOREIGN KEY (deleted_by) REFERENCES users(id) ON DELETE SET NULL;
