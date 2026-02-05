-- إضافة حقول المحافظة وحالة اكتمال الملف الشخصي لجدول المستخدمين

-- إضافة المحافظة والموقع
ALTER TABLE users ADD COLUMN IF NOT EXISTS governorate VARCHAR(50);
ALTER TABLE users ADD COLUMN IF NOT EXISTS city VARCHAR(100);

-- حقول اكتمال الملف الشخصي
ALTER TABLE users ADD COLUMN IF NOT EXISTS profile_completed BOOLEAN DEFAULT FALSE;
ALTER TABLE users ADD COLUMN IF NOT EXISTS document_verified BOOLEAN DEFAULT FALSE;
ALTER TABLE users ADD COLUMN IF NOT EXISTS verification_status VARCHAR(20) DEFAULT 'PENDING';

-- حقول الوثائق
ALTER TABLE users ADD COLUMN IF NOT EXISTS document_type VARCHAR(50);
ALTER TABLE users ADD COLUMN IF NOT EXISTS document_url VARCHAR(500);
ALTER TABLE users ADD COLUMN IF NOT EXISTS document_number VARCHAR(100);

-- حقول التحقق
ALTER TABLE users ADD COLUMN IF NOT EXISTS verified_at TIMESTAMPTZ;
ALTER TABLE users ADD COLUMN IF NOT EXISTS verified_by_admin_id UUID REFERENCES users(id) ON DELETE SET NULL;
ALTER TABLE users ADD COLUMN IF NOT EXISTS rejection_reason TEXT;

-- إنشاء فهارس
CREATE INDEX IF NOT EXISTS idx_users_governorate ON users(governorate) WHERE governorate IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_users_verification_status ON users(verification_status);
CREATE INDEX IF NOT EXISTS idx_users_profile_completed ON users(profile_completed);

-- تحديث البيانات الموجودة
UPDATE users SET profile_completed = TRUE WHERE role = 'USER';
UPDATE users SET verification_status = 'APPROVED' WHERE role = 'USER';
UPDATE users SET verification_status = 'APPROVED' WHERE role = 'WORKER';
