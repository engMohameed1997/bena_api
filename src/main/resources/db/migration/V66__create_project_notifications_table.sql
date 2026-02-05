-- إنشاء جدول إشعارات المشاريع بشكل منفصل لتجنب التعارض مع جدول notifications
CREATE TABLE IF NOT EXISTS project_notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    reference_id UUID,
    reference_type VARCHAR(50),
    is_read BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP,
    action_url VARCHAR(500),
    priority VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_project_notifications_user_id ON project_notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_project_notifications_is_read ON project_notifications(user_id, is_read);
CREATE INDEX IF NOT EXISTS idx_project_notifications_type ON project_notifications(notification_type);
CREATE INDEX IF NOT EXISTS idx_project_notifications_created_at ON project_notifications(created_at DESC);
