-- V86__create_ebooks_table.sql
-- جداول قسم الكتب الرقمية (PDF)

-- جدول الكتب الإلكترونية
CREATE TABLE IF NOT EXISTS ebooks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- الناشر (المهندس أو المصمم)
    publisher_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    -- معلومات الكتاب
    title VARCHAR(200) NOT NULL,
    description TEXT,
    cover_url VARCHAR(500),
    pdf_path VARCHAR(500) NOT NULL,
    
    -- التصنيف والسعر
    category VARCHAR(50) NOT NULL,
    price DECIMAL(10,2) NOT NULL DEFAULT 0,
    currency VARCHAR(10) DEFAULT 'IQD',
    
    -- الحالة
    is_published BOOLEAN DEFAULT TRUE,
    is_featured BOOLEAN DEFAULT FALSE,
    
    -- الإحصائيات
    total_purchases INT DEFAULT 0,
    
    -- التواريخ
    publish_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- جدول مشتريات الكتب
CREATE TABLE IF NOT EXISTS ebook_purchases (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    ebook_id UUID NOT NULL REFERENCES ebooks(id) ON DELETE CASCADE,
    
    amount_paid DECIMAL(10,2) NOT NULL,
    currency VARCHAR(10) DEFAULT 'IQD',
    
    purchased_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_opened_at TIMESTAMP,
    last_page INT DEFAULT 1,
    
    UNIQUE(user_id, ebook_id)
);

-- جدول ملاحظات المستخدم على الكتاب
CREATE TABLE IF NOT EXISTS ebook_notes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    ebook_id UUID NOT NULL REFERENCES ebooks(id) ON DELETE CASCADE,
    
    page_number INT NOT NULL,
    note_text TEXT NOT NULL,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- جدول إعدادات القارئ للمستخدم
CREATE TABLE IF NOT EXISTS ebook_reader_settings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE UNIQUE,
    
    font_size INT DEFAULT 16,
    is_dark_mode BOOLEAN DEFAULT FALSE,
    
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_ebooks_publisher ON ebooks(publisher_id);
CREATE INDEX IF NOT EXISTS idx_ebooks_category ON ebooks(category);
CREATE INDEX IF NOT EXISTS idx_ebooks_price ON ebooks(price);
CREATE INDEX IF NOT EXISTS idx_ebooks_published ON ebooks(is_published);
CREATE INDEX IF NOT EXISTS idx_ebooks_featured ON ebooks(is_featured);

CREATE INDEX IF NOT EXISTS idx_purchases_user ON ebook_purchases(user_id);
CREATE INDEX IF NOT EXISTS idx_purchases_ebook ON ebook_purchases(ebook_id);

CREATE INDEX IF NOT EXISTS idx_notes_user ON ebook_notes(user_id);
CREATE INDEX IF NOT EXISTS idx_notes_ebook ON ebook_notes(ebook_id);
CREATE INDEX IF NOT EXISTS idx_notes_page ON ebook_notes(ebook_id, page_number);
