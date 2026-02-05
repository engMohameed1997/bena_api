-- =============================================
-- عروض المقاولات - Contractor Offers Tables
-- =============================================

-- جدول العروض الرئيسي
CREATE TABLE IF NOT EXISTS contractor_offers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    worker_id BIGINT NOT NULL REFERENCES workers(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    offer_type VARCHAR(50) NOT NULL,
    base_price DECIMAL(15,2) NOT NULL,
    price_unit VARCHAR(30) DEFAULT 'PROJECT',
    min_area INTEGER,
    max_area INTEGER,
    execution_days INTEGER,
    cover_image_url VARCHAR(500),
    cover_image_data TEXT,
    is_active BOOLEAN DEFAULT true,
    is_featured BOOLEAN DEFAULT false,
    view_count INTEGER DEFAULT 0,
    city VARCHAR(100),
    area VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- مميزات العرض
CREATE TABLE IF NOT EXISTS offer_features (
    id BIGSERIAL PRIMARY KEY,
    offer_id UUID NOT NULL REFERENCES contractor_offers(id) ON DELETE CASCADE,
    feature_text VARCHAR(500) NOT NULL,
    is_included BOOLEAN DEFAULT true,
    display_order INTEGER DEFAULT 0
);

-- صور العرض
CREATE TABLE IF NOT EXISTS offer_images (
    id BIGSERIAL PRIMARY KEY,
    offer_id UUID NOT NULL REFERENCES contractor_offers(id) ON DELETE CASCADE,
    image_url VARCHAR(500),
    image_data TEXT,
    caption VARCHAR(200),
    display_order INTEGER DEFAULT 0
);

-- طلبات العروض
CREATE TABLE IF NOT EXISTS offer_requests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    offer_id UUID NOT NULL REFERENCES contractor_offers(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    message TEXT,
    phone VARCHAR(20),
    project_area INTEGER,
    status VARCHAR(30) DEFAULT 'PENDING',
    provider_notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Indexes للأداء
CREATE INDEX IF NOT EXISTS idx_offers_worker ON contractor_offers(worker_id);
CREATE INDEX IF NOT EXISTS idx_offers_type ON contractor_offers(offer_type);
CREATE INDEX IF NOT EXISTS idx_offers_active ON contractor_offers(is_active) WHERE is_active = true;
CREATE INDEX IF NOT EXISTS idx_offers_featured ON contractor_offers(is_featured) WHERE is_featured = true;
CREATE INDEX IF NOT EXISTS idx_offers_city ON contractor_offers(city);
CREATE INDEX IF NOT EXISTS idx_offers_price ON contractor_offers(base_price);
CREATE INDEX IF NOT EXISTS idx_offer_features_offer ON offer_features(offer_id);
CREATE INDEX IF NOT EXISTS idx_offer_images_offer ON offer_images(offer_id);
CREATE INDEX IF NOT EXISTS idx_offer_requests_offer ON offer_requests(offer_id);
CREATE INDEX IF NOT EXISTS idx_offer_requests_user ON offer_requests(user_id);
CREATE INDEX IF NOT EXISTS idx_offer_requests_status ON offer_requests(status);
