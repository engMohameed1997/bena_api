-- إنشاء جدول المحادثات
CREATE TABLE IF NOT EXISTS conversations (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    worker_id BIGINT NOT NULL REFERENCES workers(id) ON DELETE CASCADE,
    job_request_id BIGINT REFERENCES job_requests(id) ON DELETE SET NULL,
    last_message TEXT,
    last_message_at TIMESTAMP,
    user_unread_count INTEGER DEFAULT 0,
    worker_unread_count INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, worker_id)
);

-- إنشاء جدول الرسائل
CREATE TABLE IF NOT EXISTS messages (
    id BIGSERIAL PRIMARY KEY,
    conversation_id BIGINT NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
    sender_type VARCHAR(20) NOT NULL CHECK (sender_type IN ('USER', 'WORKER')),
    sender_id VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    message_type VARCHAR(20) DEFAULT 'TEXT' CHECK (message_type IN ('TEXT', 'IMAGE', 'LOCATION', 'FILE', 'VOICE')),
    attachment_url TEXT,
    attachment_name VARCHAR(255),
    attachment_size BIGINT,
    is_read BOOLEAN DEFAULT FALSE,
    is_delivered BOOLEAN DEFAULT FALSE,
    is_edited BOOLEAN DEFAULT FALSE,
    edited_at TIMESTAMP,
    reply_to_id BIGINT,
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- دعم قواعد بيانات قديمة: إضافة العمود إذا كان الجدول موجوداً بدون reply_to_id
ALTER TABLE messages ADD COLUMN IF NOT EXISTS reply_to_id BIGINT;

 -- دعم قواعد بيانات قديمة: أعمدة المرفقات وحالة الرسالة
 ALTER TABLE messages ADD COLUMN IF NOT EXISTS attachment_url TEXT;
 ALTER TABLE messages ADD COLUMN IF NOT EXISTS attachment_name VARCHAR(255);
 ALTER TABLE messages ADD COLUMN IF NOT EXISTS attachment_size BIGINT;
 ALTER TABLE messages ADD COLUMN IF NOT EXISTS message_type VARCHAR(20);
 ALTER TABLE messages ADD COLUMN IF NOT EXISTS is_read BOOLEAN DEFAULT FALSE;
 ALTER TABLE messages ADD COLUMN IF NOT EXISTS is_delivered BOOLEAN DEFAULT FALSE;
 ALTER TABLE messages ADD COLUMN IF NOT EXISTS is_edited BOOLEAN DEFAULT FALSE;
 ALTER TABLE messages ADD COLUMN IF NOT EXISTS edited_at TIMESTAMP;
 ALTER TABLE messages ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_messages_reply_to_id'
    ) THEN
        ALTER TABLE messages
            ADD CONSTRAINT fk_messages_reply_to_id
                FOREIGN KEY (reply_to_id) REFERENCES messages(id) ON DELETE SET NULL;
    END IF;
END $$;

-- إنشاء الفهارس
CREATE INDEX IF NOT EXISTS idx_conversations_user_id ON conversations(user_id);
CREATE INDEX IF NOT EXISTS idx_conversations_worker_id ON conversations(worker_id);
CREATE INDEX IF NOT EXISTS idx_messages_conversation_id ON messages(conversation_id);
CREATE INDEX IF NOT EXISTS idx_messages_created_at ON messages(created_at);
CREATE INDEX IF NOT EXISTS idx_messages_sender_type ON messages(sender_type);
CREATE INDEX IF NOT EXISTS idx_messages_reply_to_id ON messages(reply_to_id);

-- جدول حالة الاتصال
CREATE TABLE IF NOT EXISTS user_presence (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    is_online BOOLEAN DEFAULT FALSE,
    last_seen_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    current_conversation_id BIGINT REFERENCES conversations(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_user_presence_user_id ON user_presence(user_id);
CREATE INDEX IF NOT EXISTS idx_user_presence_is_online ON user_presence(is_online);

-- جدول مؤشر الكتابة
CREATE TABLE IF NOT EXISTS typing_indicators (
    id BIGSERIAL PRIMARY KEY,
    conversation_id BIGINT NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    is_typing BOOLEAN DEFAULT FALSE,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(conversation_id, user_id)
);
