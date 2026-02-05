CREATE TABLE IF NOT EXISTS consultation_message_ratings (
    id BIGSERIAL PRIMARY KEY,
    message_id BIGINT NOT NULL REFERENCES messages(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    worker_id BIGINT NOT NULL REFERENCES workers(id) ON DELETE CASCADE,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    helpful BOOLEAN,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, message_id)
);

CREATE INDEX IF NOT EXISTS idx_consultation_message_ratings_message_id ON consultation_message_ratings(message_id);
CREATE INDEX IF NOT EXISTS idx_consultation_message_ratings_worker_id ON consultation_message_ratings(worker_id);
CREATE INDEX IF NOT EXISTS idx_consultation_message_ratings_user_id ON consultation_message_ratings(user_id);
