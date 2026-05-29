CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE vector_store (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    content TEXT,
    metadata JSONB,
    embedding VECTOR(1536)
);

CREATE INDEX ON vector_store
USING hnsw (embedding vector_cosine_ops);
