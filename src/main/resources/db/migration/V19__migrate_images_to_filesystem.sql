-- نقل الصور من Database إلى Filesystem

-- Workers Table
ALTER TABLE workers ADD COLUMN IF NOT EXISTS profile_image_url VARCHAR(500);

-- Worker Media Table
ALTER TABLE worker_media ADD COLUMN IF NOT EXISTS media_url VARCHAR(500);
ALTER TABLE worker_media ADD COLUMN IF NOT EXISTS thumbnail_url VARCHAR(500);

-- Designs Table (قد يكون موجود)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name='designs' AND column_name='image_url') THEN
        -- العمود غير موجود، لا نفعل شيء لأنه موجود في V12
        NULL;
    END IF;
END $$;

-- ملاحظة: الأعمدة القديمة (profile_image, media_data, image_data) 
-- ستبقى للتوافق مع البيانات الموجودة
-- يمكن حذفها لاحقاً بعد نقل جميع البيانات