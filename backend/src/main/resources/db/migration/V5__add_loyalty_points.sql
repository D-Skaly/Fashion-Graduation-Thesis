-- Add loyalty_points column to users table
ALTER TABLE users ADD COLUMN loyalty_points INTEGER NOT NULL DEFAULT 0;