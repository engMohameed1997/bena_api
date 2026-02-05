-- إنشاء جدول حجز الأموال (Escrow)
CREATE TABLE IF NOT EXISTS escrow_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    milestone_id UUID REFERENCES project_milestones(id) ON DELETE SET NULL,
    payer_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    payee_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    amount DECIMAL(12, 2) NOT NULL,
    held_amount DECIMAL(12, 2) NOT NULL,
    released_amount DECIMAL(12, 2) DEFAULT 0.00,
    refunded_amount DECIMAL(12, 2) DEFAULT 0.00,
    status VARCHAR(50) NOT NULL DEFAULT 'HELD',
    held_at TIMESTAMP,
    release_scheduled_at TIMESTAMP,
    released_at TIMESTAMP,
    refunded_at TIMESTAMP,
    release_reason TEXT,
    refund_reason TEXT,
    auto_release_enabled BOOLEAN DEFAULT TRUE,
    auto_release_days INTEGER DEFAULT 7,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- إنشاء الفهارس
CREATE INDEX IF NOT EXISTS idx_escrow_project_id ON escrow_transactions(project_id);
CREATE INDEX IF NOT EXISTS idx_escrow_milestone_id ON escrow_transactions(milestone_id);
CREATE INDEX IF NOT EXISTS idx_escrow_status ON escrow_transactions(status);
CREATE INDEX IF NOT EXISTS idx_escrow_release_scheduled ON escrow_transactions(release_scheduled_at);
