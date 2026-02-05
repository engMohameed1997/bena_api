-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
-- 📱 User FCM Tokens Table
-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
-- Purpose: Store multiple FCM tokens per user (multi-device support)
-- Design: One-to-Many relationship with users table
-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

CREATE TABLE IF NOT EXISTS user_fcm_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    fcm_token TEXT NOT NULL UNIQUE,
    device_type VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_used_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
-- Indexes for Performance
-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
CREATE INDEX IF NOT EXISTS idx_user_fcm_tokens_user_id ON user_fcm_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_user_fcm_tokens_is_active ON user_fcm_tokens(is_active);
CREATE INDEX IF NOT EXISTS idx_user_fcm_tokens_user_active ON user_fcm_tokens(user_id, is_active);

-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
-- Comment
-- ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
COMMENT ON TABLE user_fcm_tokens IS 'Stores multiple FCM tokens per user for multi-device push notifications';
COMMENT ON COLUMN user_fcm_tokens.fcm_token IS 'Firebase Cloud Messaging token (UNIQUE to prevent duplicates)';
COMMENT ON COLUMN user_fcm_tokens.is_active IS 'Token validity status (FALSE when token expires/refreshes)';
COMMENT ON COLUMN user_fcm_tokens.last_used_at IS 'Last time token was used/refreshed';
