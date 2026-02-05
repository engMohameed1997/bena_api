-- إصلاح جدول notifications - إضافة عمود type إذا لم يكن موجوداً
DO $$
BEGIN
    -- التحقق من وجود عمود type
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'notifications' AND column_name = 'type'
    ) THEN
        -- إضافة عمود type
        ALTER TABLE notifications ADD COLUMN type VARCHAR(50);
        
        -- نسخ القيم من notification_type إلى type إذا كان notification_type موجوداً
        IF EXISTS (
            SELECT 1 FROM information_schema.columns 
            WHERE table_name = 'notifications' AND column_name = 'notification_type'
        ) THEN
            UPDATE notifications SET type = notification_type WHERE type IS NULL;
        END IF;
        
        -- جعل العمود NOT NULL
        ALTER TABLE notifications ALTER COLUMN type SET NOT NULL;
    END IF;
    
    -- التحقق من وجود عمود body
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'notifications' AND column_name = 'body'
    ) THEN
        -- إضافة عمود body
        ALTER TABLE notifications ADD COLUMN body TEXT;
        
        -- نسخ القيم من message إلى body إذا كان message موجوداً
        IF EXISTS (
            SELECT 1 FROM information_schema.columns 
            WHERE table_name = 'notifications' AND column_name = 'message'
        ) THEN
            UPDATE notifications SET body = message WHERE body IS NULL;
        END IF;
        
        -- جعل العمود NOT NULL
        ALTER TABLE notifications ALTER COLUMN body SET NOT NULL;
    END IF;
    
    -- التحقق من وجود عمود data
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'notifications' AND column_name = 'data'
    ) THEN
        ALTER TABLE notifications ADD COLUMN data TEXT;
    END IF;
END $$;
