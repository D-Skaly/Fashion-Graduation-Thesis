-- ============================================
-- Database Optimizations V6
-- ============================================

-- 1. Add composite index for order status queries (high priority)
CREATE INDEX IF NOT EXISTS idx_orders_status_user_created 
ON orders(status, user_id, created_at DESC);

-- 2. Add index for product price filtering
CREATE INDEX IF NOT EXISTS idx_products_price_range 
ON products(base_price) WHERE is_active = true;

-- 3. Add composite index for cart with user + updated
CREATE INDEX IF NOT EXISTS idx_cart_user_updated 
ON carts(user_id, updated_at DESC) WHERE user_id IS NOT NULL;

-- 4. Add index for payment lookup by order
CREATE INDEX IF NOT EXISTS idx_payments_order_id 
ON payments(order_id);

-- 5. Add index for coupon code lookup
CREATE INDEX IF NOT EXISTS idx_coupons_code 
ON coupons(code) WHERE deleted_at IS NULL;

-- 6. Add index for product category + price sorting
CREATE INDEX IF NOT EXISTS idx_products_category_price 
ON products(category_id, base_price) WHERE is_active = true;

-- 7. Add index for reviews with product + recent
CREATE INDEX IF NOT EXISTS idx_reviews_product_recent 
ON reviews(product_id, created_at DESC);

-- 8. Add index for wishlist user + created
CREATE INDEX IF NOT EXISTS idx_wishlists_user_created 
ON wishlists(user_id, created_at DESC);

-- 9. Optimize outbox events for polling
CREATE INDEX IF NOT EXISTS idx_outbox_status_created 
ON outbox_events(status, created_at) WHERE status = 'PENDING';

-- 10. Add index for try_on_jobs status + user
CREATE INDEX IF NOT EXISTS idx_tryon_status_user 
ON try_on_jobs(status, user_id) WHERE status = 'PENDING';

-- 11. Add partial index for customer profiles with style
CREATE INDEX IF NOT EXISTS idx_customer_profiles_style 
ON customer_profiles(customer_id) WHERE style_preference_vector IS NOT NULL;

-- 12. Add index for analytics recent dates
CREATE INDEX IF NOT EXISTS idx_analytics_recent 
ON analytics(analytics_date DESC, product_id);

-- 13. Add index for shipping order lookup
CREATE INDEX IF NOT EXISTS idx_shipping_order 
ON shipping(order_id);

-- 14. Add index for order_items for inventory tracking (commented out - column status may not exist)
-- CREATE INDEX IF NOT EXISTS idx_order_items_product_qty 
-- ON order_items(product_variant_id, quantity) WHERE status = 'COMPLETED';

-- 15. Payment callback ledger optimization
CREATE INDEX IF NOT EXISTS idx_payment_ledger_method_lookup 
ON payment_callback_ledger(payment_method, lookup_transaction_id);