-- V87: إضافة cover_data و cover_type للكتب
ALTER TABLE ebooks ADD COLUMN IF NOT EXISTS cover_data BYTEA;
ALTER TABLE ebooks ADD COLUMN IF NOT EXISTS cover_type VARCHAR(50);
