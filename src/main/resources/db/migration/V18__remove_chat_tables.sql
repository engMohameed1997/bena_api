-- حذف جداول المحادثات والرسائل
DROP TABLE IF EXISTS messages CASCADE;
DROP TABLE IF EXISTS conversations CASCADE;

-- حذف الفهارس المتعلقة بالمحادثات
DROP INDEX IF EXISTS idx_messages_conversation;
