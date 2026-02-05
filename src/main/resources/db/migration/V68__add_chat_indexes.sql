-- تحسين فهارس نظام المحادثات لتحسين الأداء

-- conversations: تسريع جلب محادثات المستخدم/المختص حسب last_message_at
CREATE INDEX IF NOT EXISTS idx_conversations_user_last_message_at
    ON conversations(user_id, last_message_at DESC);

CREATE INDEX IF NOT EXISTS idx_conversations_worker_last_message_at
    ON conversations(worker_id, last_message_at DESC);

-- messages: تسريع جلب الرسائل + استبعاد المحذوف + الترتيب بالوقت
CREATE INDEX IF NOT EXISTS idx_messages_conversation_created_at
    ON messages(conversation_id, created_at DESC);

-- دعم قواعد بيانات قديمة: إضافة العمود إذا كان الجدول موجوداً بدون is_deleted
ALTER TABLE messages ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN DEFAULT FALSE;

CREATE INDEX IF NOT EXISTS idx_messages_conversation_not_deleted_created_at
    ON messages(conversation_id, is_deleted, created_at DESC);

-- typing_indicators: تسريع جلب من يكتب داخل المحادثة
CREATE INDEX IF NOT EXISTS idx_typing_indicators_conversation_is_typing
    ON typing_indicators(conversation_id, is_typing);

-- user_presence: تسريع استعلامات الحالة
CREATE INDEX IF NOT EXISTS idx_user_presence_user_is_online
    ON user_presence(user_id, is_online);
