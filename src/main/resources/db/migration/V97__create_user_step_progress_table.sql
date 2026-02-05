CREATE TABLE IF NOT EXISTS user_step_progress (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    step_id BIGINT NOT NULL,
    step_title VARCHAR(255) NOT NULL,
    is_completed BOOLEAN DEFAULT FALSE,
    completed_at TIMESTAMP WITH TIME ZONE,
    notes TEXT,
    actual_cost DECIMAL(12, 2),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, step_id)
);

CREATE INDEX IF NOT EXISTS idx_user_step_progress_user_id ON user_step_progress(user_id);
CREATE INDEX IF NOT EXISTS idx_user_step_progress_step_id ON user_step_progress(step_id);
