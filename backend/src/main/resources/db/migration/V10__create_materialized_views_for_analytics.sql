-- Create materialized views for analytics and reporting
-- Materialized views improve performance for complex aggregations by pre-computing results

-- Materialized view for daily revenue statistics
CREATE MATERIALIZED VIEW mv_daily_revenue AS
SELECT 
    DATE(p.created_at) as date,
    COUNT(*) as total_orders,
    SUM(o.total_amount) as total_revenue,
    AVG(o.total_amount) as average_order_value,
    COUNT(DISTINCT o.user_id) as unique_customers,
    COUNT(CASE WHEN p.status = 'COMPLETED' THEN 1 END) as completed_orders,
    SUM(CASE WHEN p.status = 'COMPLETED' THEN o.total_amount ELSE 0 END) as completed_revenue
FROM orders o
JOIN payments p ON o.id = p.order_id
WHERE o.status != 'CANCELLED'
GROUP BY DATE(p.created_at)
WITH DATA;

-- Create unique index on date for refresh operations
CREATE UNIQUE INDEX mv_daily_revenue_date_idx ON mv_daily_revenue (date);

-- Materialized view for monthly revenue by category
CREATE MATERIALIZED VIEW mv_monthly_revenue_by_category AS
SELECT 
    DATE_TRUNC('month', p.created_at) as month,
    c.name as category_name,
    COUNT(DISTINCT o.id) as total_orders,
    SUM(o.total_amount) as total_revenue,
    AVG(o.total_amount) as average_order_value,
    SUM(o.sold_count) as total_items_sold
FROM orders o
JOIN payments p ON o.id = p.order_id
JOIN products pr ON o.id = pr.id
JOIN categories c ON pr.category_id = c.id
WHERE p.status = 'COMPLETED' AND o.status != 'CANCELLED'
GROUP BY DATE_TRUNC('month', p.created_at), c.name
WITH DATA;

CREATE UNIQUE INDEX mv_monthly_revenue_by_category_idx ON mv_monthly_revenue_by_category (month, category_name);

-- Materialized view for product performance metrics
CREATE MATERIALIZED VIEW mv_product_performance AS
SELECT 
    p.id as product_id,
    p.name as product_name,
    p.sku,
    c.name as category_name,
    p.view_count,
    p.sold_count,
    p.rating_avg,
    COUNT(DISTINCT o.id) as total_orders,
    SUM(o.total_amount) as total_revenue,
    AVG(o.total_amount) as average_order_value,
    COUNT(CASE WHEN r.id IS NOT NULL THEN 1 END) as total_reviews,
    AVG(r.rating) as avg_review_rating,
    COUNT(DISTINCT CASE WHEN w.id IS NOT NULL THEN w.user_id END) as wishlist_count
FROM products p
LEFT JOIN categories c ON p.category_id = c.id
LEFT JOIN order_items oi ON p.id = oi.product_id
LEFT JOIN orders o ON oi.order_id = o.id AND o.status != 'CANCELLED'
LEFT JOIN payments pay ON o.id = pay.order_id AND pay.status = 'COMPLETED'
LEFT JOIN reviews r ON p.id = r.product_id
LEFT JOIN wishlists w ON p.id = w.product_id
WHERE p.is_active = true
GROUP BY p.id, p.name, p.sku, c.name, p.view_count, p.sold_count, p.rating_avg
WITH DATA;

CREATE UNIQUE INDEX mv_product_performance_idx ON mv_product_performance (product_id);

-- Materialized view for customer lifetime value (CLV)
CREATE MATERIALIZED VIEW mv_customer_lifetime_value AS
SELECT 
    u.id as user_id,
    u.email,
    u.first_name,
    u.last_name,
    u.created_at as registration_date,
    u.last_login_at,
    COUNT(DISTINCT o.id) as total_orders,
    SUM(p.amount) as total_spent,
    AVG(p.amount) as average_order_value,
    MIN(o.created_at) as first_order_date,
    MAX(o.created_at) as last_order_date,
    EXTRACT(DAY FROM (MAX(o.created_at) - MIN(o.created_at))) as customer_lifetime_days
FROM users u
LEFT JOIN orders o ON u.id = o.user_id AND o.status != 'CANCELLED'
LEFT JOIN payments p ON o.id = p.order_id AND p.status = 'COMPLETED'
GROUP BY u.id, u.email, u.first_name, u.last_name, u.created_at, u.last_login_at
WITH DATA;

CREATE UNIQUE INDEX mv_customer_lifetime_value_idx ON mv_customer_lifetime_value (user_id);

-- Materialized view for payment method statistics
CREATE MATERIALIZED VIEW mv_payment_method_stats AS
SELECT 
    DATE_TRUNC('month', p.created_at) as month,
    p.method as payment_method,
    COUNT(*) as total_transactions,
    SUM(p.amount) as total_amount,
    AVG(p.amount) as average_amount,
    COUNT(CASE WHEN p.status = 'COMPLETED' THEN 1 END) as successful_transactions,
    COUNT(CASE WHEN p.status = 'FAILED' THEN 1 END) as failed_transactions,
    ROUND(COUNT(CASE WHEN p.status = 'COMPLETED' THEN 1 END)::numeric / COUNT(*) * 100, 2) as success_rate
FROM payments p
GROUP BY DATE_TRUNC('month', p.created_at), p.method
WITH DATA;

CREATE UNIQUE INDEX mv_payment_method_stats_idx ON mv_payment_method_stats (month, payment_method);

-- Materialized view for inventory alerts (low stock products)
CREATE MATERIALIZED VIEW mv_low_stock_alerts AS
SELECT 
    p.id as product_id,
    p.name as product_name,
    p.sku,
    c.name as category_name,
    pv.size,
    pv.color,
    pv.stock_quantity,
    p.sold_count,
    CASE 
        WHEN pv.stock_quantity = 0 THEN 'OUT_OF_STOCK'
        WHEN pv.stock_quantity <= 5 THEN 'CRITICAL'
        WHEN pv.stock_quantity <= 10 THEN 'LOW'
        ELSE 'OK'
    END as stock_status,
    p.created_at as product_created_date
FROM products p
JOIN categories c ON p.category_id = c.id
JOIN product_variants pv ON p.id = pv.product_id
WHERE p.is_active = true AND pv.stock_quantity <= 10
ORDER BY pv.stock_quantity ASC
WITH DATA;

CREATE INDEX mv_low_stock_alerts_stock_status_idx ON mv_low_stock_alerts (stock_status);

-- Function to refresh all materialized views
CREATE OR REPLACE FUNCTION refresh_all_materialized_views()
RETURNS VOID AS $$
BEGIN
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_daily_revenue;
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_monthly_revenue_by_category;
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_product_performance;
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_customer_lifetime_value;
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_payment_method_stats;
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_low_stock_alerts;
END;
$$ LANGUAGE plpgsql;

-- Create a scheduled job to refresh materialized views daily
-- This would typically be handled by pg_cron or an external scheduler
-- For now, we'll create the function that can be called manually or via application
