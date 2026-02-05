-- توحيد عمود نوع الإشعار: إبقاء العمود type فقط وجعله NOT NULL
DO $$
BEGIN
    -- إذا كان يوجد عمود notification_type ولا يوجد عمود type -> نعيد التسمية
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'notifications' AND column_name = 'notification_type'
    ) AND NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'notifications' AND column_name = 'type'
    ) THEN
        ALTER TABLE notifications RENAME COLUMN notification_type TO type;
    END IF;

    -- إذا كان العمودان موجودين معاً -> ننسخ القيم ثم نحذف القديم
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'notifications' AND column_name = 'type'
    ) AND EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'notifications' AND column_name = 'notification_type'
    ) THEN
        -- نسخ البيانات من القديم إلى الجديد إذا كانت القيمة NULL
        UPDATE notifications SET type = notification_type WHERE type IS NULL AND notification_type IS NOT NULL;
        -- حذف العمود القديم
        ALTER TABLE notifications DROP COLUMN notification_type;
    END IF;

    -- التأكد أن العمود type موجود وليس NULL
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'notifications' AND column_name = 'type'
    ) THEN
        -- تعبئة القيم NULL بقيمة افتراضية SYSTEM
        UPDATE notifications SET type = 'SYSTEM' WHERE type IS NULL;
        ALTER TABLE notifications ALTER COLUMN type SET NOT NULL;
    END IF;
END $$;
