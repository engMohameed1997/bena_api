-- مزامنة أعمدة جدول notifications
DO $$
BEGIN
    -- نسخ البيانات من notification_type إلى type إذا كان type موجوداً وفارغاً
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'notifications' AND column_name = 'type'
    ) AND EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'notifications' AND column_name = 'notification_type'
    ) THEN
        UPDATE notifications SET type = notification_type WHERE type IS NULL AND notification_type IS NOT NULL;
        UPDATE notifications SET notification_type = type WHERE notification_type IS NULL AND type IS NOT NULL;
    END IF;
    
    -- إذا كان notification_type موجوداً وtype موجود، نحذف notification_type
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'notifications' AND column_name = 'notification_type'
    ) AND EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'notifications' AND column_name = 'type'
    ) THEN
        -- نسخ البيانات أولاً
        UPDATE notifications SET type = notification_type WHERE type IS NULL;
        -- حذف العمود القديم
        ALTER TABLE notifications DROP COLUMN IF EXISTS notification_type;
    END IF;
    
    -- إذا كان notification_type موجوداً فقط، نعيد تسميته
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'notifications' AND column_name = 'notification_type'
    ) AND NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'notifications' AND column_name = 'type'
    ) THEN
        ALTER TABLE notifications RENAME COLUMN notification_type TO type;
    END IF;
    
    -- التأكد من أن type ليس NULL
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'notifications' AND column_name = 'type'
    ) THEN
        ALTER TABLE notifications ALTER COLUMN type SET NOT NULL;
    END IF;
END $$;
