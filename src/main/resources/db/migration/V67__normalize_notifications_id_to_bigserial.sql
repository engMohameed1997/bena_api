-- توحيد نوع المفتاح الأساسي لجدول notifications ليتوافق مع كيان النظام (BIGSERIAL)
DO $$
DECLARE
    has_title BOOLEAN;
    has_body BOOLEAN;
    has_message BOOLEAN;
    has_type BOOLEAN;
    has_notification_type BOOLEAN;
    has_reference_id BOOLEAN;
    has_reference_type BOOLEAN;
    has_is_read BOOLEAN;
    has_data BOOLEAN;
    has_created_at BOOLEAN;

    body_expr TEXT;
    type_expr TEXT;
    title_expr TEXT;
    message_legacy_expr TEXT;
    notification_type_legacy_expr TEXT;
    reference_id_expr TEXT;
    reference_type_expr TEXT;
    is_read_expr TEXT;
    data_expr TEXT;
    created_at_expr TEXT;
    insert_sql TEXT;
BEGIN
    -- فقط إذا كان id من نوع UUID (schema قديم)
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'notifications' AND column_name = 'id' AND data_type = 'uuid'
    ) THEN
        has_title := EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'notifications' AND column_name = 'title');
        has_body := EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'notifications' AND column_name = 'body');
        has_message := EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'notifications' AND column_name = 'message');
        has_type := EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'notifications' AND column_name = 'type');
        has_notification_type := EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'notifications' AND column_name = 'notification_type');
        has_reference_id := EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'notifications' AND column_name = 'reference_id');
        has_reference_type := EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'notifications' AND column_name = 'reference_type');
        has_is_read := EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'notifications' AND column_name = 'is_read');
        has_data := EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'notifications' AND column_name = 'data');
        has_created_at := EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'notifications' AND column_name = 'created_at');

        title_expr := CASE WHEN has_title THEN 'COALESCE(title, ''إشعار جديد'')' ELSE '''إشعار جديد''' END;

        body_expr := '''[رسالة إشعار]''';
        IF has_body AND has_message THEN
            body_expr := 'COALESCE(body, message, ''[رسالة إشعار]'')';
        ELSIF has_body THEN
            body_expr := 'COALESCE(body, ''[رسالة إشعار]'')';
        ELSIF has_message THEN
            body_expr := 'COALESCE(message, ''[رسالة إشعار]'')';
        END IF;

        type_expr := '''SYSTEM''';
        IF has_type AND has_notification_type THEN
            type_expr := 'COALESCE(type, notification_type, ''SYSTEM'')';
        ELSIF has_type THEN
            type_expr := 'COALESCE(type, ''SYSTEM'')';
        ELSIF has_notification_type THEN
            type_expr := 'COALESCE(notification_type, ''SYSTEM'')';
        END IF;

        message_legacy_expr := CASE WHEN has_message THEN 'message' ELSE 'NULL' END;
        notification_type_legacy_expr := CASE WHEN has_notification_type THEN 'notification_type' ELSE 'NULL' END;

        -- reference_id كان UUID في بعض النسخ القديمة، لا يمكن تحويله إلى BIGINT بأمان
        reference_id_expr := 'NULL';
        IF has_reference_id THEN
            reference_id_expr := 'NULL';
        END IF;

        reference_type_expr := CASE WHEN has_reference_type THEN 'reference_type' ELSE 'NULL' END;
        is_read_expr := CASE WHEN has_is_read THEN 'COALESCE(is_read, FALSE)' ELSE 'FALSE' END;
        data_expr := CASE WHEN has_data THEN 'data' ELSE 'NULL' END;
        created_at_expr := CASE WHEN has_created_at THEN 'created_at' ELSE 'CURRENT_TIMESTAMP' END;

        -- إعادة تسمية الجدول القديم ثم إنشاء الجدول الجديد
        ALTER TABLE notifications RENAME TO notifications_old;

        CREATE TABLE notifications (
            id BIGSERIAL PRIMARY KEY,
            user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
            title VARCHAR(255) NOT NULL,
            body TEXT NOT NULL,
            message TEXT,
            type VARCHAR(50) NOT NULL,
            notification_type VARCHAR(50),
            reference_id BIGINT,
            reference_type VARCHAR(50),
            is_read BOOLEAN DEFAULT FALSE,
            data TEXT,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );

        CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notifications(user_id);
        CREATE INDEX IF NOT EXISTS idx_notifications_is_read ON notifications(user_id, is_read);
        CREATE INDEX IF NOT EXISTS idx_notifications_created_at ON notifications(created_at DESC);

        insert_sql := 'INSERT INTO notifications (user_id, title, body, message, type, notification_type, reference_id, reference_type, is_read, data, created_at) '
            || 'SELECT user_id, '
            || title_expr || ', '
            || body_expr || ', '
            || message_legacy_expr || ', '
            || type_expr || ', '
            || notification_type_legacy_expr || ', '
            || reference_id_expr || ', '
            || reference_type_expr || ', '
            || is_read_expr || ', '
            || data_expr || ', '
            || created_at_expr || ' '
            || 'FROM notifications_old';

        EXECUTE insert_sql;

        DROP TABLE notifications_old;
    END IF;
END $$;
