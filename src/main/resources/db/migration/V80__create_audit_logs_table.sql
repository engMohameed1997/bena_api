-- Flyway Migration: Create Audit Logs Table
-- Purpose: Store all sensitive operations for security, compliance, and analysis

CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGSERIAL PRIMARY KEY,
    
    -- User who performed the action (null for anonymous)
    user_id UUID,
    user_email VARCHAR(100),
    
    -- What happened
    action VARCHAR(50) NOT NULL,
    target_type VARCHAR(30) NOT NULL,
    target_id VARCHAR(50),
    description TEXT,
    
    -- Change tracking
    old_value TEXT,
    new_value TEXT,
    
    -- Request context
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    request_path VARCHAR(200),
    request_method VARCHAR(10),
    
    -- Status
    status VARCHAR(20) DEFAULT 'SUCCESS',
    error_message TEXT,
    
    -- Timestamp
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Indexes for efficient querying
CREATE INDEX IF NOT EXISTS idx_audit_user_id ON audit_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_action ON audit_logs(action);
CREATE INDEX IF NOT EXISTS idx_audit_target_type ON audit_logs(target_type);
CREATE INDEX IF NOT EXISTS idx_audit_created_at ON audit_logs(created_at);
CREATE INDEX IF NOT EXISTS idx_audit_ip_address ON audit_logs(ip_address);

-- Composite index for entity lookup
CREATE INDEX IF NOT EXISTS idx_audit_target ON audit_logs(target_type, target_id);

-- Partial index for failure analysis
CREATE INDEX IF NOT EXISTS idx_audit_failures ON audit_logs(action, created_at) WHERE status = 'FAILURE';

-- Comment
COMMENT ON TABLE audit_logs IS 'Stores all sensitive operations for security, compliance, and analysis';
