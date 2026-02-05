-- إضافة عداد المحاولات الفاشلة لرموز التحقق/إعادة التعيين

ALTER TABLE email_verification_tokens
    ADD COLUMN IF NOT EXISTS failed_attempts INTEGER NOT NULL DEFAULT 0;

ALTER TABLE password_reset_tokens
    ADD COLUMN IF NOT EXISTS failed_attempts INTEGER NOT NULL DEFAULT 0;
