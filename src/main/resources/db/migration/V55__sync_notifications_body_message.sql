-- توحيد أعمدة notifications بين body و message
DO $$
BEGIN
    -- إذا كان عمود message موجوداً وعمود body موجوداً
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'notifications' AND column_name = 'message')
       AND EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'notifications' AND column_name = 'body') THEN
        -- نسخ البيانات من message إلى body إذا كان body فارغاً
        UPDATE notifications SET body = message WHERE (body IS NULL OR body = '') AND message IS NOT NULL;
        -- حذف العمود القديم
        ALTER TABLE notifications DROP COLUMN message;
    END IF;

    -- إذا كان عمود message موجوداً فقط بدون body
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'notifications' AND column_name = 'message')
       AND NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'notifications' AND column_name = 'body') THEN
        ALTER TABLE notifications RENAME COLUMN message TO body;
    END IF;

    -- التأكد من أن body ليس NULL
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'notifications' AND column_name = 'body') THEN
        ALTER TABLE notifications ALTER COLUMN body SET NOT NULL;
    END IF;
END $$;
