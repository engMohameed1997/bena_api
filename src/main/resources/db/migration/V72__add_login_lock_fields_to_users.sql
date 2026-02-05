-- إضافة حقول قفل الحساب ومحاولات تسجيل الدخول الفاشلة

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS failed_login_attempts INTEGER NOT NULL DEFAULT 0;

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS lock_time TIMESTAMPTZ;
