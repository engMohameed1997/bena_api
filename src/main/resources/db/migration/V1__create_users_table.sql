-- V1: Create users table with advanced indexing

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- B-Tree Unique Index for email lookup
CREATE UNIQUE INDEX idx_users_email ON users(email);

-- B-Tree Index for phone lookup
CREATE INDEX idx_users_phone ON users(phone) WHERE phone IS NOT NULL;

-- Partial Index for active users only (optimizes common queries)
CREATE INDEX idx_users_active ON users(role, created_at) WHERE is_active = TRUE;

-- BRIN Index for time-series queries (very efficient for sequential data)
CREATE INDEX idx_users_created_brin ON users USING BRIN(created_at);
