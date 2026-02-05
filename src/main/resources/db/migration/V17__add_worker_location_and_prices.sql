-- إضافة حقول الموقع الجغرافي للعمال
ALTER TABLE workers ADD COLUMN IF NOT EXISTS city VARCHAR(100);
ALTER TABLE workers ADD COLUMN IF NOT EXISTS area VARCHAR(100);
ALTER TABLE workers ADD COLUMN IF NOT EXISTS latitude DOUBLE PRECISION;
ALTER TABLE workers ADD COLUMN IF NOT EXISTS longitude DOUBLE PRECISION;

-- إضافة حقول الأسعار
ALTER TABLE workers ADD COLUMN IF NOT EXISTS price_per_meter DECIMAL(10,2);
ALTER TABLE workers ADD COLUMN IF NOT EXISTS price_per_day DECIMAL(10,2);
ALTER TABLE workers ADD COLUMN IF NOT EXISTS price_per_visit DECIMAL(10,2);

-- إضافة حقول إضافية للعامل
ALTER TABLE workers ADD COLUMN IF NOT EXISTS works_at_night BOOLEAN DEFAULT FALSE;
ALTER TABLE workers ADD COLUMN IF NOT EXISTS estimated_completion_days INTEGER;

-- إضافة حقل كلمة المرور للعامل (للدخول إلى Dashboard)
ALTER TABLE workers ADD COLUMN IF NOT EXISTS password_hash VARCHAR(255);
ALTER TABLE workers ADD COLUMN IF NOT EXISTS email VARCHAR(255);

-- جدول طلبات العمل
CREATE TABLE IF NOT EXISTS job_requests (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    worker_id BIGINT NOT NULL,
    job_type VARCHAR(100) NOT NULL,
    description TEXT,
    location_city VARCHAR(100),
    location_area VARCHAR(100),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    budget DECIMAL(10,2),
    status VARCHAR(50) DEFAULT 'PENDING',
    worker_response TEXT,
    worker_price_offer DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (worker_id) REFERENCES workers(id) ON DELETE CASCADE
);

-- جدول صور طلبات العمل
CREATE TABLE IF NOT EXISTS job_request_images (
    id BIGSERIAL PRIMARY KEY,
    job_request_id BIGINT NOT NULL,
    image_data BYTEA,
    content_type VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (job_request_id) REFERENCES job_requests(id) ON DELETE CASCADE
);

-- جدول المحادثات
CREATE TABLE IF NOT EXISTS conversations (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    worker_id BIGINT NOT NULL,
    last_message_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (worker_id) REFERENCES workers(id) ON DELETE CASCADE,
    UNIQUE(user_id, worker_id)
);

-- جدول الرسائل
CREATE TABLE IF NOT EXISTS messages (
    id BIGSERIAL PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    sender_type VARCHAR(20) NOT NULL, -- 'USER' or 'WORKER'
    sender_id BIGINT NOT NULL,
    content TEXT,
    image_data BYTEA,
    content_type VARCHAR(100),
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE
);

-- جدول البلاغات
CREATE TABLE IF NOT EXISTS reports (
    id BIGSERIAL PRIMARY KEY,
    reporter_id BIGINT NOT NULL,
    worker_id BIGINT NOT NULL,
    report_type VARCHAR(50) NOT NULL, -- 'WRONG_NUMBER', 'FRAUD', 'OFFENSIVE', 'UNPROFESSIONAL'
    description TEXT,
    status VARCHAR(50) DEFAULT 'PENDING', -- 'PENDING', 'REVIEWED', 'RESOLVED', 'DISMISSED'
    admin_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (worker_id) REFERENCES workers(id) ON DELETE CASCADE
);

-- فهارس للبحث السريع
CREATE INDEX IF NOT EXISTS idx_workers_city ON workers(city);
CREATE INDEX IF NOT EXISTS idx_workers_location ON workers(latitude, longitude);
CREATE INDEX IF NOT EXISTS idx_workers_price_meter ON workers(price_per_meter);
CREATE INDEX IF NOT EXISTS idx_workers_price_day ON workers(price_per_day);
CREATE INDEX IF NOT EXISTS idx_job_requests_status ON job_requests(status);
CREATE INDEX IF NOT EXISTS idx_messages_conversation ON messages(conversation_id);
CREATE INDEX IF NOT EXISTS idx_reports_status ON reports(status);
