-- إضافة أنواع الإشعارات الخاصة بالعقود إلى قيد التحقق في جدول notifications
DO $$
BEGIN
    -- أولاً التحقق من وجود القيد وتحديثه
    IF EXISTS (
        SELECT 1 FROM information_schema.check_constraints 
        WHERE constraint_name = 'notifications_notification_type_check'
    ) THEN
        -- حذف القيد القديم
        ALTER TABLE notifications DROP CONSTRAINT notifications_notification_type_check;
        
        -- إضافة قيد جديد مع جميع أنواع الإشعارات بما فيها أنواع العقود
        ALTER TABLE notifications ADD CONSTRAINT notifications_notification_type_check 
        CHECK (notification_type IN (
            'JOB_REQUEST', 
            'REQUEST_ACCEPTED', 
            'REQUEST_REJECTED', 
            'PRICE_OFFER', 
            'MESSAGE', 
            'PAYMENT', 
            'SYSTEM',
            'CONTRACT_PENDING',
            'CONTRACT_SIGNED', 
            'CONTRACT_ACTIVE',
            'CONTRACT_REJECTED'
        ));
    END IF;
    
    -- التحقق أيضاً من عمود type إذا كان موجوداً
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'notifications' AND column_name = 'type'
    ) AND EXISTS (
        SELECT 1 FROM information_schema.check_constraints 
        WHERE constraint_name = 'notifications_type_check'
    ) THEN
        -- حذف القيد القديم
        ALTER TABLE notifications DROP CONSTRAINT notifications_type_check;
        
        -- إضافة قيد جديد مع جميع أنواع الإشعارات
        ALTER TABLE notifications ADD CONSTRAINT notifications_type_check 
        CHECK (type IN (
            'JOB_REQUEST', 
            'REQUEST_ACCEPTED', 
            'REQUEST_REJECTED', 
            'PRICE_OFFER', 
            'MESSAGE', 
            'PAYMENT', 
            'SYSTEM',
            'CONTRACT_PENDING',
            'CONTRACT_SIGNED', 
            'CONTRACT_ACTIVE',
            'CONTRACT_REJECTED'
        ));
    END IF;
END $$;
