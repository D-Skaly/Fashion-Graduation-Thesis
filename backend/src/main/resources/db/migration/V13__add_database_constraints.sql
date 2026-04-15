-- Add CHECK constraints for data integrity
ALTER TABLE products ADD CONSTRAINT chk_products_price_positive 
    CHECK (base_price >= 0 AND discount_price >= 0);
ALTER TABLE products ADD CONSTRAINT chk_products_stock_non_negative 
    CHECK (stock >= 0);
ALTER TABLE products ADD CONSTRAINT chk_products_discount_valid 
    CHECK (discount_price <= base_price OR discount_price IS NULL);

ALTER TABLE orders ADD CONSTRAINT chk_orders_total_positive 
    CHECK (total_amount >= 0);
ALTER TABLE orders ADD CONSTRAINT chk_orders_status_valid 
    CHECK (status IN ('PENDING', 'CONFIRMED', 'SHIPPING', 'DELIVERED', 'CANCELLED', 'REFUNDED'));

ALTER TABLE payments ADD CONSTRAINT chk_payments_amount_positive 
    CHECK (amount >= 0);
ALTER TABLE payments ADD CONSTRAINT chk_payments_status_valid 
    CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED'));

ALTER TABLE cart_items ADD CONSTRAINT chk_cart_items_quantity_positive 
    CHECK (quantity > 0);

ALTER TABLE product_variants ADD CONSTRAINT chk_variants_stock_non_negative 
    CHECK (stock >= 0);
ALTER TABLE product_variants ADD CONSTRAINT chk_variants_price_positive 
    CHECK (price >= 0);

ALTER TABLE coupons ADD CONSTRAINT chk_coupons_discount_positive 
    CHECK (discount_value > 0);
ALTER TABLE coupons ADD CONSTRAINT chk_coupons_discount_range 
    CHECK (discount_value <= 100 AND discount_type = 'PERCENT' OR discount_type = 'FIXED');
ALTER TABLE coupons ADD CONSTRAINT chk_coupons_dates_valid 
    CHECK (valid_from < valid_to);

ALTER TABLE reviews ADD CONSTRAINT chk_reviews_rating_valid 
    CHECK (rating >= 1 AND rating <= 5);

-- Add CASCADE DELETE for foreign keys (where appropriate)
-- Note: Most foreign keys should use SET NULL or RESTRICT to prevent accidental data loss
-- CASCADE DELETE is only used for child records that should be deleted when parent is deleted

-- Cart items should be deleted when cart is deleted (temporary data)
ALTER TABLE cart_items DROP CONSTRAINT IF EXISTS fk_cart_items_cart;
ALTER TABLE cart_items ADD CONSTRAINT fk_cart_items_cart 
    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE;

-- Order items should be deleted when order is deleted
ALTER TABLE order_items DROP CONSTRAINT IF EXISTS fk_order_items_order;
ALTER TABLE order_items ADD CONSTRAINT fk_order_items_order 
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE;

-- Add NOT NULL constraints for critical fields
ALTER TABLE products ALTER COLUMN name SET NOT NULL;
ALTER TABLE products ALTER COLUMN base_price SET NOT NULL;
ALTER TABLE products ALTER COLUMN sku SET NOT NULL;

ALTER TABLE orders ALTER COLUMN user_id SET NOT NULL;
ALTER TABLE orders ALTER COLUMN total_amount SET NOT NULL;
ALTER TABLE orders ALTER COLUMN status SET NOT NULL;

ALTER TABLE users ALTER COLUMN email SET NOT NULL;
ALTER TABLE users ALTER COLUMN password SET NOT NULL;

ALTER TABLE categories ALTER COLUMN name SET NOT NULL;

-- Add UNIQUE constraints for unique business keys
ALTER TABLE products ADD CONSTRAINT uk_products_sku UNIQUE (sku);
ALTER TABLE users ADD CONSTRAINT uk_users_email UNIQUE (email);
ALTER TABLE coupons ADD CONSTRAINT uk_coupons_code UNIQUE (code);
