-- جدول العمال والخلف
CREATE TABLE IF NOT EXISTS workers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL,
    description TEXT,
    phone_number VARCHAR(20),
    whatsapp_number VARCHAR(20),
    profile_image BYTEA,
    profile_image_type VARCHAR(50),
    average_rating DOUBLE PRECISION DEFAULT 0.0,
    review_count INTEGER DEFAULT 0,
    is_featured BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    experience_years INTEGER,
    location VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- جدول وسائط العامل (صور/فيديوهات الأعمال)
CREATE TABLE IF NOT EXISTS worker_media (
    id BIGSERIAL PRIMARY KEY,
    worker_id BIGINT NOT NULL REFERENCES workers(id) ON DELETE CASCADE,
    media_type VARCHAR(20) NOT NULL,
    media_data BYTEA,
    content_type VARCHAR(50),
    external_url VARCHAR(500),
    caption VARCHAR(255),
    display_order INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- جدول تقييمات العمال
CREATE TABLE IF NOT EXISTS worker_reviews (
    id BIGSERIAL PRIMARY KEY,
    worker_id BIGINT NOT NULL REFERENCES workers(id) ON DELETE CASCADE,
    reviewer_name VARCHAR(255) NOT NULL,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    is_approved BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- فهارس لتحسين الأداء
CREATE INDEX IF NOT EXISTS idx_workers_category ON workers(category);
CREATE INDEX IF NOT EXISTS idx_workers_is_active ON workers(is_active);
CREATE INDEX IF NOT EXISTS idx_workers_is_featured ON workers(is_featured);
CREATE INDEX IF NOT EXISTS idx_workers_rating ON workers(average_rating);
CREATE INDEX IF NOT EXISTS idx_worker_media_worker_id ON worker_media(worker_id);
CREATE INDEX IF NOT EXISTS idx_worker_reviews_worker_id ON worker_reviews(worker_id);
