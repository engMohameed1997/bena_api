-- إضافة حقل تأكيد البريد الإلكتروني للمستخدمين
ALTER TABLE users ADD COLUMN IF NOT EXISTS email_verified BOOLEAN DEFAULT FALSE;

-- جدول رموز تأكيد البريد الإلكتروني
CREATE TABLE IF NOT EXISTS email_verification_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(100) NOT NULL UNIQUE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    expires_at TIMESTAMP NOT NULL,
    verified_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- فهارس للبحث السريع
CREATE INDEX IF NOT EXISTS idx_email_verification_token ON email_verification_tokens(token);
CREATE INDEX IF NOT EXISTS idx_email_verification_user ON email_verification_tokens(user_id);

-- إصلاح جدول password_reset_tokens إذا كان موجوداً بنوع خاطئ
DO $$
BEGIN
    -- حذف الجدول القديم إذا كان موجوداً بنوع خاطئ
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'password_reset_tokens' 
        AND column_name = 'user_id' 
        AND data_type = 'bigint'
    ) THEN
        DROP TABLE IF EXISTS password_reset_tokens CASCADE;
        
        CREATE TABLE password_reset_tokens (
            id BIGSERIAL PRIMARY KEY,
            token VARCHAR(100) NOT NULL UNIQUE,
            user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
            expires_at TIMESTAMP NOT NULL,
            used_at TIMESTAMP,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );
        
        CREATE INDEX idx_password_reset_token ON password_reset_tokens(token);
        CREATE INDEX idx_password_reset_user ON password_reset_tokens(user_id);
    END IF;
END $$;
