-- إضافة حقل صورة الملف الشخصي لجدول المستخدمين

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS profile_picture_url VARCHAR(500);
