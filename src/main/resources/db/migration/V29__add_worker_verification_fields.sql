-- إضافة حقول التوثيق لجدول العمال
ALTER TABLE workers ADD COLUMN IF NOT EXISTS is_verified BOOLEAN DEFAULT FALSE;
ALTER TABLE workers ADD COLUMN IF NOT EXISTS verification_status VARCHAR(50) DEFAULT 'PENDING';
ALTER TABLE workers ADD COLUMN IF NOT EXISTS id_card_number VARCHAR(50);
ALTER TABLE workers ADD COLUMN IF NOT EXISTS id_card_image_url VARCHAR(500);
ALTER TABLE workers ADD COLUMN IF NOT EXISTS license_number VARCHAR(50);
ALTER TABLE workers ADD COLUMN IF NOT EXISTS license_image_url VARCHAR(500);
ALTER TABLE workers ADD COLUMN IF NOT EXISTS certificate_urls TEXT;
ALTER TABLE workers ADD COLUMN IF NOT EXISTS verification_notes TEXT;
ALTER TABLE workers ADD COLUMN IF NOT EXISTS verified_at TIMESTAMP;
ALTER TABLE workers ADD COLUMN IF NOT EXISTS verified_by_admin_id UUID REFERENCES users(id) ON DELETE SET NULL;

-- إنشاء فهرس
CREATE INDEX IF NOT EXISTS idx_workers_verification_status ON workers(verification_status);
CREATE INDEX IF NOT EXISTS idx_workers_is_verified ON workers(is_verified);
