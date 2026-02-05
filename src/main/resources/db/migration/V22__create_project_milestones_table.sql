-- إنشاء جدول مراحل المشروع
CREATE TABLE IF NOT EXISTS project_milestones (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    milestone_order INTEGER NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    expected_completion_date TIMESTAMP,
    actual_completion_date TIMESTAMP,
    client_approved BOOLEAN DEFAULT FALSE,
    client_approval_date TIMESTAMP,
    payment_released BOOLEAN DEFAULT FALSE,
    payment_release_date TIMESTAMP,
    work_evidence_urls TEXT,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- إنشاء الفهارس
CREATE INDEX IF NOT EXISTS idx_milestones_project_id ON project_milestones(project_id);
CREATE INDEX IF NOT EXISTS idx_milestones_status ON project_milestones(status);
CREATE INDEX IF NOT EXISTS idx_milestones_order ON project_milestones(project_id, milestone_order);
