-- Flyway Migration: Re-create Refresh Tokens Table to fix ID type mismatch
-- Purpose: Ensure ID is BIGSERIAL (Long) not UUID

DROP TABLE IF EXISTS refresh_tokens CASCADE;

CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    
    -- Token value (unique, indexed)
    token VARCHAR(255) NOT NULL UNIQUE,
    
    -- User reference
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    -- Device/Session info
    device_info VARCHAR(500),
    ip_address VARCHAR(45),
    
    -- Expiration
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    
    -- Revocation
    is_revoked BOOLEAN DEFAULT FALSE,
    revoked_at TIMESTAMP WITH TIME ZONE,
    revoked_reason VARCHAR(100),
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_used_at TIMESTAMP WITH TIME ZONE
);

-- Indexes
CREATE INDEX idx_refresh_token ON refresh_tokens(token);
CREATE INDEX idx_refresh_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_expires_at ON refresh_tokens(expires_at);
CREATE INDEX idx_refresh_active ON refresh_tokens(user_id, is_revoked, expires_at) WHERE is_revoked = FALSE;
