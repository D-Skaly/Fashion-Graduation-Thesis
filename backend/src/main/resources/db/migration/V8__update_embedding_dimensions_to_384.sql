-- Update embedding dimensions from 768 (Gemini) to 384 (Transformers all-MiniLM-L6-v2)
-- We drop and recreate the column to ensure the vector dimension constraint is updated.

ALTER TABLE products DROP COLUMN IF EXISTS embedding_vector;
ALTER TABLE products ADD COLUMN embedding_vector vector(384);
