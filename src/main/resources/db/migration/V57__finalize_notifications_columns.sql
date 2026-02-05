-- تنظيف نهائي لجدول notifications: توحيد الأعمدة وإزالة الحقول القديمة
DO $$
BEGIN
    -- 1) إذا كان عمود message موجوداً: انسخ إلى body ثم احذفه
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'notifications' AND column_name = 'message') THEN
        -- إنشاء body لو غير موجود
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'notifications' AND column_name = 'body') THEN
            ALTER TABLE notifications ADD COLUMN body TEXT;
        END IF;
        -- نسخ البيانات
        UPDATE notifications SET body = message WHERE (body IS NULL OR body = '') AND message IS NOT NULL;
        -- حذف message
        ALTER TABLE notifications DROP COLUMN message;
    END IF;

    -- 2) التأكد من وجود body وغير فارغ
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'notifications' AND column_name = 'body') THEN
        ALTER TABLE notifications ADD COLUMN body TEXT;
    END IF;
    UPDATE notifications SET body = '[رسالة إشعار]' WHERE body IS NULL OR body = '';
    ALTER TABLE notifications ALTER COLUMN body SET NOT NULL;

    -- 3) إذا كان notification_type موجوداً: انسخ إلى type ثم احذفه
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'notifications' AND column_name = 'notification_type') THEN
        -- إنشاء type لو غير موجود
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'notifications' AND column_name = 'type') THEN
            ALTER TABLE notifications ADD COLUMN type VARCHAR(50);
        END IF;
        -- نسخ القيم
        UPDATE notifications SET type = notification_type WHERE type IS NULL AND notification_type IS NOT NULL;
        -- حذف العمود القديم
        ALTER TABLE notifications DROP COLUMN notification_type;
    END IF;

    -- 4) التأكد من وجود type وغير فارغ
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'notifications' AND column_name = 'type') THEN
        ALTER TABLE notifications ADD COLUMN type VARCHAR(50);
    END IF;
    UPDATE notifications SET type = 'SYSTEM' WHERE type IS NULL OR type = '';
    ALTER TABLE notifications ALTER COLUMN type SET NOT NULL;
END $$;
