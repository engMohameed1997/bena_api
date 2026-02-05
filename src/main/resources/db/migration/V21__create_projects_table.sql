-- إنشاء جدول المشاريع
CREATE TABLE IF NOT EXISTS projects (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    provider_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    project_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    total_budget DECIMAL(12, 2) NOT NULL,
    platform_commission_percentage DECIMAL(5, 2) DEFAULT 10.00,
    platform_commission_amount DECIMAL(12, 2),
    provider_amount DECIMAL(12, 2),
    location_city VARCHAR(100),
    location_area VARCHAR(100),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    start_date TIMESTAMP,
    expected_end_date TIMESTAMP,
    actual_end_date TIMESTAMP,
    client_rating INTEGER CHECK (client_rating >= 1 AND client_rating <= 5),
    client_review TEXT,
    provider_rating INTEGER CHECK (provider_rating >= 1 AND provider_rating <= 5),
    provider_review TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- إنشاء الفهارس
CREATE INDEX IF NOT EXISTS idx_projects_client_id ON projects(client_id);
CREATE INDEX IF NOT EXISTS idx_projects_provider_id ON projects(provider_id);
CREATE INDEX IF NOT EXISTS idx_projects_status ON projects(status);
CREATE INDEX IF NOT EXISTS idx_projects_project_type ON projects(project_type);
CREATE INDEX IF NOT EXISTS idx_projects_created_at ON projects(created_at);
