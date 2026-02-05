ALTER TABLE messages ADD COLUMN IF NOT EXISTS attachment_url TEXT;
ALTER TABLE messages ADD COLUMN IF NOT EXISTS attachment_name VARCHAR(255);
ALTER TABLE messages ADD COLUMN IF NOT EXISTS attachment_size BIGINT;
ALTER TABLE messages ADD COLUMN IF NOT EXISTS message_type VARCHAR(20);
ALTER TABLE messages ADD COLUMN IF NOT EXISTS is_read BOOLEAN DEFAULT FALSE;
ALTER TABLE messages ADD COLUMN IF NOT EXISTS is_delivered BOOLEAN DEFAULT FALSE;
ALTER TABLE messages ADD COLUMN IF NOT EXISTS is_edited BOOLEAN DEFAULT FALSE;
ALTER TABLE messages ADD COLUMN IF NOT EXISTS edited_at TIMESTAMP;
ALTER TABLE messages ADD COLUMN IF NOT EXISTS reply_to_id BIGINT;
ALTER TABLE messages ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN DEFAULT FALSE;
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
