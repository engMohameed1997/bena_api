-- Flyway Migration: Create Feature Flags Table
-- Purpose: Store feature flags for dynamic feature management

CREATE TABLE IF NOT EXISTS feature_flags (
    id BIGSERIAL PRIMARY KEY,
    
    -- Feature identification
    feature_key VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    
    -- Activation
    is_enabled BOOLEAN DEFAULT FALSE,
    rollout_percentage INTEGER CHECK (rollout_percentage >= 0 AND rollout_percentage <= 100),
    
    -- Organization
    category VARCHAR(50),
    metadata TEXT,
    
    -- Audit
    updated_by UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Index for key lookup
CREATE INDEX IF NOT EXISTS idx_feature_flag_key ON feature_flags(feature_key);
CREATE INDEX IF NOT EXISTS idx_feature_flag_category ON feature_flags(category);
CREATE INDEX IF NOT EXISTS idx_feature_flag_enabled ON feature_flags(is_enabled);

-- Insert default feature flags
INSERT INTO feature_flags (feature_key, name, description, category, is_enabled) VALUES
    ('ai.image.generation', 'توليد الصور بالذكاء الاصطناعي', 'تفعيل ميزة توليد الصور باستخدام AI', 'AI', true),
    ('ai.text.generation', 'توليد النصوص بالذكاء الاصطناعي', 'تفعيل ميزة توليد النصوص باستخدام AI', 'AI', true),
    ('chat.enabled', 'نظام المحادثة', 'تفعيل نظام المحادثة بين المستخدمين', 'Communication', true),
    ('push.notifications', 'الإشعارات الفورية', 'تفعيل إرسال الإشعارات الفورية', 'Communication', true),
    ('payment.escrow', 'نظام الضمان المالي', 'تفعيل نظام Escrow للمدفوعات', 'Payment', true),
    ('worker.verification', 'توثيق العمال', 'تفعيل نظام التحقق من وثائق العمال', 'Verification', true),
    ('search.advanced', 'البحث المتقدم', 'تفعيل ميزات البحث المتقدم', 'Search', true),
    ('system.maintenance', 'وضع الصيانة', 'تفعيل وضع الصيانة للموقع', 'System', false)
ON CONFLICT (feature_key) DO NOTHING;

-- Comment
COMMENT ON TABLE feature_flags IS 'Stores feature flags for dynamic feature management without deployment';
