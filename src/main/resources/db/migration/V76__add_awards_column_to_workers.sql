-- إضافة عمود awards المفقود في جدول العمال لضمان تطابق المخطط مع الكيان
ALTER TABLE workers ADD COLUMN IF NOT EXISTS awards TEXT;
