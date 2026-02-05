-- إزالة الأعمدة الجديدة والإبقاء على الأعمدة القديمة فقط
DO $$
BEGIN
    -- إذا كان عمود body موجوداً: انسخ محتواه إلى message ثم احذفه
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'notifications' AND column_name = 'body') THEN
        -- إذا لم يكن عمود message موجوداً، قم بإنشائه أولاً
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'notifications' AND column_name = 'message') THEN
            ALTER TABLE notifications ADD COLUMN message TEXT;
        END IF;

        -- نسخ البيانات من body إلى message إذا كان message فارغاً
        UPDATE notifications SET message = body WHERE (message IS NULL OR message = '') AND body IS NOT NULL;
        -- حذف عمود body
        ALTER TABLE notifications DROP COLUMN body;
    END IF;

    -- إذا كان عمود type موجوداً: انسخ محتواه إلى notification_type ثم احذفه
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'notifications' AND column_name = 'type') THEN
        -- إذا لم يكن عمود notification_type موجوداً، قم بإنشائه أولاً
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'notifications' AND column_name = 'notification_type') THEN
            ALTER TABLE notifications ADD COLUMN notification_type VARCHAR(50);
        END IF;

        -- نسخ البيانات من type إلى notification_type إذا كان notification_type فارغاً
        UPDATE notifications SET notification_type = type WHERE (notification_type IS NULL OR notification_type = '') AND type IS NOT NULL;
        -- حذف عمود type
        ALTER TABLE notifications DROP COLUMN type;
    END IF;

    -- التأكد من أن message ليس NULL
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'notifications' AND column_name = 'message') THEN
        UPDATE notifications SET message = '[رسالة إشعار]' WHERE message IS NULL OR message = '';
        ALTER TABLE notifications ALTER COLUMN message SET NOT NULL;
    END IF;

    -- التأكد من أن notification_type ليس NULL
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'notifications' AND column_name = 'notification_type') THEN
        UPDATE notifications SET notification_type = 'SYSTEM' WHERE notification_type IS NULL OR notification_type = '';
        ALTER TABLE notifications ALTER COLUMN notification_type SET NOT NULL;
    END IF;
END $$;
