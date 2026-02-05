-- إزالة الأعمدة القديمة وضبط الأعمدة المطلوبة في notifications بشكل نهائي
DO $$
BEGIN
    -- إسقاط عمود notification_type إن وجد
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'notifications' AND column_name = 'notification_type') THEN
        ALTER TABLE notifications DROP COLUMN notification_type;
    END IF;

    -- إسقاط عمود message إن وجد (تم استبداله بـ body)
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'notifications' AND column_name = 'message') THEN
        ALTER TABLE notifications DROP COLUMN message;
    END IF;

    -- التأكد من وجود العمود body وأنه ليس فارغاً
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'notifications' AND column_name = 'body') THEN
        ALTER TABLE notifications ADD COLUMN body TEXT;
    END IF;
    UPDATE notifications SET body = '[رسالة إشعار]' WHERE body IS NULL OR body = '';
    ALTER TABLE notifications ALTER COLUMN body SET NOT NULL;

    -- التأكد من وجود العمود type وأنه ليس فارغاً
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'notifications' AND column_name = 'type') THEN
        ALTER TABLE notifications ADD COLUMN type VARCHAR(50);
    END IF;
    UPDATE notifications SET type = 'SYSTEM' WHERE type IS NULL OR type = '';
    ALTER TABLE notifications ALTER COLUMN type SET NOT NULL;
END $$;
