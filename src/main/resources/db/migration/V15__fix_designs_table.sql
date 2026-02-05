-- إصلاح جدول التصاميم

-- حذف الجداول المرتبطة أولاً
DROP TABLE IF EXISTS design_materials CASCADE;
DROP TABLE IF EXISTS design_features CASCADE;
DROP TABLE IF EXISTS designs CASCADE;

-- إعادة إنشاء جدول التصاميم بالأنواع الصحيحة
CREATE TABLE designs (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    image_url VARCHAR(500),
    image_data BYTEA,
    image_type VARCHAR(50),
    category VARCHAR(50) NOT NULL,
    style VARCHAR(50) NOT NULL,
    area_sqm INTEGER,
    estimated_cost DOUBLE PRECISION,
    view_count INTEGER DEFAULT 0,
    is_featured BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- إنشاء جدول المواد المستخدمة في التصاميم
CREATE TABLE design_materials (
    design_id BIGINT NOT NULL,
    material VARCHAR(255),
    FOREIGN KEY (design_id) REFERENCES designs(id) ON DELETE CASCADE
);

-- إنشاء جدول مميزات التصاميم
CREATE TABLE design_features (
    design_id BIGINT NOT NULL,
    feature VARCHAR(255),
    FOREIGN KEY (design_id) REFERENCES designs(id) ON DELETE CASCADE
);

-- إنشاء فهارس للبحث السريع
CREATE INDEX idx_designs_category ON designs(category);
CREATE INDEX idx_designs_style ON designs(style);
CREATE INDEX idx_designs_is_featured ON designs(is_featured);
CREATE INDEX idx_designs_is_active ON designs(is_active);
