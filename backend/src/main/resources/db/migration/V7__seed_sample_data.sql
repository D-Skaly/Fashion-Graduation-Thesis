-- Seeding Sample Categories
INSERT INTO categories (id, name, slug, description) VALUES
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Men', 'men', 'Premium collection for men'),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'Women', 'women', 'Elegant collection for women'),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'Accessories', 'accessories', 'Luxury fashion accessories')
ON CONFLICT (slug) DO NOTHING;

-- Seeding Sample Products
INSERT INTO products (id, name, description, base_price, category_id, created_at, updated_at) VALUES
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380b11', 'Classic White Shirt', 'A timeless classic white shirt made from 100% organic cotton.', 49.99, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', NOW(), NOW()),
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380b12', 'Slim Fit Navy Suit', 'Modern slim fit suit in deep navy blue, perfect for formal occasions.', 299.00, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', NOW(), NOW()),
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380b21', 'Silk Wrap Dress', 'Elegant silk wrap dress with floral patterns.', 129.99, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', NOW(), NOW()),
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380b22', 'Cashmere Sweater', 'Ultra-soft cashmere sweater for cozy winter days.', 189.00, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Seeding Product Variants
INSERT INTO product_variants (id, product_id, size, color, stock_quantity, price_adjustment, sku_code) VALUES
('c0eebc99-9c0b-4ef8-bb6d-6bb9bd380c11', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380b11', 'M', 'White', 50, 0.00, 'SHIRT-WHT-M'),
('c0eebc99-9c0b-4ef8-bb6d-6bb9bd380c12', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380b11', 'L', 'White', 30, 0.00, 'SHIRT-WHT-L'),
('c0eebc99-9c0b-4ef8-bb6d-6bb9bd380c21', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380b12', '48R', 'Navy', 10, 0.00, 'SUIT-NVY-48R'),
('c0eebc99-9c0b-4ef8-bb6d-6bb9bd380c22', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380b12', '50R', 'Navy', 5, 0.00, 'SUIT-NVY-50R')
ON CONFLICT (sku_code) DO NOTHING;
