-- إنشاء جدول الإبلاغات
CREATE TABLE IF NOT EXISTS disputes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    raised_by_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    against_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    dispute_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    evidence_urls TEXT,
    assigned_admin_id UUID REFERENCES users(id) ON DELETE SET NULL,
    admin_notes TEXT,
    resolution_details TEXT,
    resolution_outcome VARCHAR(50),
    payment_held BOOLEAN DEFAULT FALSE,
    resolved_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- إنشاء الفهارس
CREATE INDEX IF NOT EXISTS idx_disputes_project_id ON disputes(project_id);
CREATE INDEX IF NOT EXISTS idx_disputes_raised_by_id ON disputes(raised_by_id);
CREATE INDEX IF NOT EXISTS idx_disputes_against_id ON disputes(against_id);
CREATE INDEX IF NOT EXISTS idx_disputes_status ON disputes(status);
CREATE INDEX IF NOT EXISTS idx_disputes_assigned_admin_id ON disputes(assigned_admin_id);
CREATE INDEX IF NOT EXISTS idx_disputes_created_at ON disputes(created_at);
