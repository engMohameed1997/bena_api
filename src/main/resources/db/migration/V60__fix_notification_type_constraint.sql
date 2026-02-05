-- إصلاح مشكلة الـ check constraint في جدول notifications
DO $$
BEGIN
    -- 1) إزالة أي check constraint قديم باسم notifications_type_check أو بالاسم السابق
    IF EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'notifications_type_check') THEN
        ALTER TABLE notifications DROP CONSTRAINT notifications_type_check;
    END IF;
    IF EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'notifications_notification_type_check') THEN
        ALTER TABLE notifications DROP CONSTRAINT notifications_notification_type_check;
    END IF;

    -- 2) التأكد من أن العمود type موجود (وليس notification_type)
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'notifications' AND column_name = 'type') THEN
        -- إذا لم يكن موجوداً، ننشئه وننقل البيانات من notification_type إن وجد
        ALTER TABLE notifications ADD COLUMN type VARCHAR(50);
        IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'notifications' AND column_name = 'notification_type') THEN
            UPDATE notifications SET type = notification_type WHERE type IS NULL AND notification_type IS NOT NULL;
            ALTER TABLE notifications DROP COLUMN notification_type;
        END IF;
    END IF;

    -- 3) تحديث القيم الفارغة
    UPDATE notifications SET type = 'SYSTEM' WHERE type IS NULL OR type = '';
    ALTER TABLE notifications ALTER COLUMN type SET NOT NULL;

    -- 4) إضافة check constraint جديد على العمود type
    ALTER TABLE notifications ADD CONSTRAINT notifications_type_check
        CHECK (type IN ('JOB_REQUEST', 'REQUEST_ACCEPTED', 'REQUEST_REJECTED', 'PRICE_OFFER', 'MESSAGE', 'PAYMENT', 'SYSTEM', 'CONTRACT_PENDING', 'CONTRACT_SIGNED', 'CONTRACT_ACTIVE', 'CONTRACT_REJECTED'));
END $$;
