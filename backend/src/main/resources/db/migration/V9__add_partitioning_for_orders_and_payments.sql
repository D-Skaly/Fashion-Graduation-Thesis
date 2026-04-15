-- Partition orders table by created_at (monthly partitions)
-- This improves query performance for time-based queries and reduces index size

-- First, create a partitioned version of orders table
CREATE TABLE orders_partitioned (
    LIKE orders INCLUDING ALL
) PARTITION BY RANGE (created_at);

-- Create partitions for current year (example: 2024)
CREATE TABLE orders_2024_01 PARTITION OF orders_partitioned
    FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');

CREATE TABLE orders_2024_02 PARTITION OF orders_partitioned
    FOR VALUES FROM ('2024-02-01') TO ('2024-03-01');

CREATE TABLE orders_2024_03 PARTITION OF orders_partitioned
    FOR VALUES FROM ('2024-03-01') TO ('2024-04-01');

CREATE TABLE orders_2024_04 PARTITION OF orders_partitioned
    FOR VALUES FROM ('2024-04-01') TO ('2024-05-01');

CREATE TABLE orders_2024_05 PARTITION OF orders_partitioned
    FOR VALUES FROM ('2024-05-01') TO ('2024-06-01');

CREATE TABLE orders_2024_06 PARTITION OF orders_partitioned
    FOR VALUES FROM ('2024-06-01') TO ('2024-07-01');

CREATE TABLE orders_2024_07 PARTITION OF orders_partitioned
    FOR VALUES FROM ('2024-07-01') TO ('2024-08-01');

CREATE TABLE orders_2024_08 PARTITION OF orders_partitioned
    FOR VALUES FROM ('2024-08-01') TO ('2024-09-01');

CREATE TABLE orders_2024_09 PARTITION OF orders_partitioned
    FOR VALUES FROM ('2024-09-01') TO ('2024-10-01');

CREATE TABLE orders_2024_10 PARTITION OF orders_partitioned
    FOR VALUES FROM ('2024-10-01') TO ('2024-11-01');

CREATE TABLE orders_2024_11 PARTITION OF orders_partitioned
    FOR VALUES FROM ('2024-11-01') TO ('2024-12-01');

CREATE TABLE orders_2024_12 PARTITION OF orders_partitioned
    FOR VALUES FROM ('2024-12-01') TO ('2025-01-01');

-- Create default partition for future data
CREATE TABLE orders_future PARTITION OF orders_partitioned
    DEFAULT;

-- Copy data from original table to partitioned table
INSERT INTO orders_partitioned SELECT * FROM orders;

-- Drop original table and rename partitioned table
DROP TABLE orders;
ALTER TABLE orders_partitioned RENAME TO orders;

-- Partition payments table by created_at (monthly partitions)
CREATE TABLE payments_partitioned (
    LIKE payments INCLUDING ALL
) PARTITION BY RANGE (created_at);

-- Create partitions for current year
CREATE TABLE payments_2024_01 PARTITION OF payments_partitioned
    FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');

CREATE TABLE payments_2024_02 PARTITION OF payments_partitioned
    FOR VALUES FROM ('2024-02-01') TO ('2024-03-01');

CREATE TABLE payments_2024_03 PARTITION OF payments_partitioned
    FOR VALUES FROM ('2024-03-01') TO ('2024-04-01');

CREATE TABLE payments_2024_04 PARTITION OF payments_partitioned
    FOR VALUES FROM ('2024-04-01') TO ('2024-05-01');

CREATE TABLE payments_2024_05 PARTITION OF payments_partitioned
    FOR VALUES FROM ('2024-05-01') TO ('2024-06-01');

CREATE TABLE payments_2024_06 PARTITION OF payments_partitioned
    FOR VALUES FROM ('2024-06-01') TO ('2024-07-01');

CREATE TABLE payments_2024_07 PARTITION OF payments_partitioned
    FOR VALUES FROM ('2024-07-01') TO ('2024-08-01');

CREATE TABLE payments_2024_08 PARTITION OF payments_partitioned
    FOR VALUES FROM ('2024-08-01') TO ('2024-09-01');

CREATE TABLE payments_2024_09 PARTITION OF payments_partitioned
    FOR VALUES FROM ('2024-09-01') TO ('2024-10-01');

CREATE TABLE payments_2024_10 PARTITION OF payments_partitioned
    FOR VALUES FROM ('2024-10-01') TO ('2024-11-01');

CREATE TABLE payments_2024_11 PARTITION OF payments_partitioned
    FOR VALUES FROM ('2024-11-01') TO ('2024-12-01');

CREATE TABLE payments_2024_12 PARTITION OF payments_partitioned
    FOR VALUES FROM ('2024-12-01') TO ('2025-01-01');

-- Create default partition for future data
CREATE TABLE payments_future PARTITION OF payments_partitioned
    DEFAULT;

-- Copy data from original table to partitioned table
INSERT INTO payments_partitioned SELECT * FROM payments;

-- Drop original table and rename partitioned table
DROP TABLE payments;
ALTER TABLE payments_partitioned RENAME TO payments;

-- Create indexes on partitioned tables (indexes are inherited but need to be created on each partition)
-- These will be automatically created on new partitions via a trigger or function

-- Create function to automatically create partitions for new months
CREATE OR REPLACE FUNCTION create_monthly_partition(table_name TEXT, partition_date DATE)
RETURNS VOID AS $$
DECLARE
    partition_name TEXT;
    start_date TEXT;
    end_date TEXT;
BEGIN
    partition_name := table_name || '_' || to_char(partition_date, 'YYYY_MM');
    start_date := to_char(date_trunc('month', partition_date), 'YYYY-MM-DD');
    end_date := to_char(date_trunc('month', partition_date) + INTERVAL '1 month', 'YYYY-MM-DD');
    
    EXECUTE format('CREATE TABLE IF NOT EXISTS %I PARTITION OF %I FOR VALUES FROM (%L) TO (%L)',
                   partition_name, table_name, start_date, end_date);
END;
$$ LANGUAGE plpgsql;

-- Create trigger to automatically create partitions for orders
CREATE OR REPLACE FUNCTION auto_create_orders_partition()
RETURNS TRIGGER AS $$
BEGIN
    PERFORM create_monthly_partition('orders', NEW.created_at);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_auto_create_orders_partition
    BEFORE INSERT ON orders
    FOR EACH ROW
    EXECUTE FUNCTION auto_create_orders_partition();

-- Create trigger to automatically create partitions for payments
CREATE OR REPLACE FUNCTION auto_create_payments_partition()
RETURNS TRIGGER AS $$
BEGIN
    PERFORM create_monthly_partition('payments', NEW.created_at);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_auto_create_payments_partition
    BEFORE INSERT ON payments
    FOR EACH ROW
    EXECUTE FUNCTION auto_create_payments_partition();
