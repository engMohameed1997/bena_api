CREATE TABLE app_notifications (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    user_id UUID,
    type VARCHAR(255) NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_app_notification_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_app_notification_user_id ON app_notifications(user_id);
CREATE INDEX idx_app_notification_created_at ON app_notifications(created_at);
CREATE INDEX idx_app_notification_read ON app_notifications(is_read);
