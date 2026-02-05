-- إنشاء جدول العروض
CREATE TABLE IF NOT EXISTS bids (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    provider_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    service_type VARCHAR(50) NOT NULL,
    offered_price DECIMAL(12, 2) NOT NULL,
    estimated_duration_days INTEGER,
    proposal_details TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    client_response TEXT,
    response_date TIMESTAMP,
    converted_to_project_id UUID,
    location_city VARCHAR(100),
    location_area VARCHAR(100),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    expires_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- إنشاء الفهارس
CREATE INDEX IF NOT EXISTS idx_bids_client_id ON bids(client_id);
CREATE INDEX IF NOT EXISTS idx_bids_provider_id ON bids(provider_id);
CREATE INDEX IF NOT EXISTS idx_bids_status ON bids(status);
CREATE INDEX IF NOT EXISTS idx_bids_service_type ON bids(service_type);
CREATE INDEX IF NOT EXISTS idx_bids_expires_at ON bids(expires_at);
CREATE INDEX IF NOT EXISTS idx_bids_created_at ON bids(created_at);
