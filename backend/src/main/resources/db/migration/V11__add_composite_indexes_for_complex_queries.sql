-- Add composite indexes for complex queries that involve filtering and sorting
-- Composite indexes improve performance for queries that filter on multiple columns or filter + sort

-- Products: Composite indexes for common filter + sort patterns
CREATE INDEX idx_products_category_active_featured_created 
    ON products(category_id, is_active, is_featured, created_at DESC);

CREATE INDEX idx_products_brand_active_price 
    ON products(brand, is_active, base_price) 
    WHERE is_active = true;

CREATE INDEX idx_products_tags_active_rating 
    ON products USING GIN(tags) 
    WHERE is_active = true;

CREATE INDEX idx_products_search_fulltext 
    ON products USING GIN(to_tsvector('english', name || ' ' || description || ' ' || coalesce(brand, ''))) 
    WHERE is_active = true;

-- Orders: Composite indexes for user queries with status and date sorting
CREATE INDEX idx_orders_user_status_created 
    ON orders(user_id, status, created_at DESC);

CREATE INDEX idx_orders_status_created_total 
    ON orders(status, created_at DESC, total_amount);

CREATE INDEX idx_orders_user_created_range 
    ON orders(user_id, created_at DESC, total_amount DESC);

-- Payments: Composite indexes for payment analytics
CREATE INDEX idx_payments_status_method_created 
    ON payments(status, method, created_at DESC);

CREATE INDEX idx_payments_order_status_amount 
    ON payments(order_id, status, amount);

CREATE INDEX idx_payments_transaction_status 
    ON payments(transaction_id, status) 
    WHERE transaction_id IS NOT NULL;

-- Cart: Composite indexes for cart operations
CREATE INDEX idx_cart_user_updated 
    ON carts(user_id, updated_at DESC) 
    WHERE user_id IS NOT NULL;

CREATE INDEX idx_cart_guest_updated 
    ON carts(guest_id, updated_at DESC) 
    WHERE guest_id IS NOT NULL;

-- Cart Items: Composite indexes for cart item queries
CREATE INDEX idx_cart_items_cart_variant 
    ON cart_items(cart_id, product_variant_id);

CREATE INDEX idx_cart_items_cart_added 
    ON cart_items(cart_id, added_at DESC);

-- Reviews: Composite indexes for review filtering and sorting
CREATE INDEX idx_reviews_product_rating_created 
    ON reviews(product_id, rating, created_at DESC);

CREATE INDEX idx_reviews_user_product_created 
    ON reviews(user_id, product_id, created_at DESC);

CREATE INDEX idx_reviews_product_verified_helpful 
    ON reviews(product_id, is_verified_purchase, helpful_count DESC);

-- Wishlist: Composite indexes for wishlist queries
CREATE INDEX idx_wishlist_user_created 
    ON wishlists(user_id, created_at DESC);

CREATE INDEX idx_wishlist_product_created 
    ON wishlists(product_id, created_at DESC);

-- Product Variants: Composite indexes for variant filtering
CREATE INDEX idx_product_variants_product_stock_price 
    ON product_variants(product_id, stock_quantity, price) 
    WHERE stock_quantity > 0;

CREATE INDEX idx_product_variants_product_size_color 
    ON product_variants(product_id, size, color);

-- Shipping: Composite indexes for tracking queries
CREATE INDEX idx_shipping_order_status 
    ON shipping(order_id, status);

CREATE INDEX idx_shipping_tracking_carrier 
    ON shipping(tracking_number, carrier) 
    WHERE tracking_number IS NOT NULL;

-- Order Status History: Composite indexes for history queries
CREATE INDEX idx_order_status_history_order_created 
    ON order_status_history(order_id, created_at DESC);

CREATE INDEX idx_order_status_history_status_created 
    ON order_status_history(status, created_at DESC);

-- Order Items: Composite indexes for order item analytics
CREATE INDEX idx_order_items_order_quantity 
    ON order_items(order_id, quantity, price);

CREATE INDEX idx_order_items_product_quantity 
    ON order_items(product_id, quantity);

-- Coupons: Composite indexes for coupon validation
CREATE INDEX idx_coupons_code_active_dates 
    ON coupons(code, is_active, start_date, end_date);

CREATE INDEX idx_coupons_active_usage 
    ON coupons(is_active, usage_count, max_usage) 
    WHERE is_active = true;

-- Addresses: Composite indexes for address queries
CREATE INDEX idx_addresses_user_type_default 
    ON addresses(user_id, type, is_default);

-- Chat Sessions: Composite indexes for chat queries
CREATE INDEX idx_chat_sessions_user_active_created 
    ON chat_sessions(user_id, is_active, created_at DESC);

-- Chat Messages: Composite indexes for message queries
CREATE INDEX idx_chat_messages_session_role_created 
    ON chat_messages(session_id, role, created_at ASC);

-- Create partial indexes for frequently filtered data
CREATE INDEX idx_products_featured_only 
    ON products(created_at DESC) 
    WHERE is_featured = true AND is_active = true;

CREATE INDEX idx_products_new_arrivals 
    ON products(created_at DESC) 
    WHERE is_active = true AND created_at > NOW() - INTERVAL '30 days';

CREATE INDEX idx_orders_pending 
    ON orders(created_at DESC) 
    WHERE status = 'PENDING';

CREATE INDEX idx_orders_processing 
    ON orders(created_at DESC) 
    WHERE status = 'PROCESSING';

CREATE INDEX idx_payments_pending 
    ON payments(created_at DESC) 
    WHERE status = 'PENDING';

-- Create expression indexes for computed columns
CREATE INDEX idx_products_price_range 
    ON products((base_price / 100)) 
    WHERE is_active = true;

CREATE INDEX idx_orders_total_range 
    ON orders((total_amount / 1000000)) 
    WHERE status != 'CANCELLED';

-- Analyze tables to update statistics after index creation
ANALYZE products;
ANALYZE orders;
ANALYZE payments;
ANALYZE carts;
ANALYZE cart_items;
ANALYZE reviews;
ANALYZE wishlists;
ANALYZE product_variants;
ANALYZE shipping;
ANALYZE order_status_history;
ANALYZE order_items;
ANALYZE coupons;
ANALYZE addresses;
ANALYZE chat_sessions;
ANALYZE chat_messages;
