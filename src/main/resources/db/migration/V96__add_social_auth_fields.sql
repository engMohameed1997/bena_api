-- إضافة حقول المصادقة الاجتماعية
-- Add Social Authentication Fields

-- إضافة حقل Google ID
ALTER TABLE users ADD COLUMN IF NOT EXISTS google_id VARCHAR(255) UNIQUE;

-- إضافة حقل Apple ID  
ALTER TABLE users ADD COLUMN IF NOT EXISTS apple_id VARCHAR(255) UNIQUE;

-- إضافة حقل صورة الملف الشخصي
ALTER TABLE users ADD COLUMN IF NOT EXISTS profile_picture VARCHAR(500);

-- تحديث حقل كلمة المرور ليكون اختياري (للمستخدمين الاجتماعيين)
ALTER TABLE users ALTER COLUMN password_hash DROP NOT NULL;

-- إنشاء فهارس للبحث السريع
CREATE INDEX IF NOT EXISTS idx_users_google_id ON users(google_id);
CREATE INDEX IF NOT EXISTS idx_users_apple_id ON users(apple_id);

COMMENT ON COLUMN users.google_id IS 'معرف المستخدم من Google - Google User ID';
COMMENT ON COLUMN users.apple_id IS 'معرف المستخدم من Apple - Apple User ID';
COMMENT ON COLUMN users.profile_picture IS 'صورة الملف الشخصي من التسجيل الاجتماعي';
