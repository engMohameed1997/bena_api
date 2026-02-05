-- إنشاء جدول العقود
CREATE TABLE IF NOT EXISTS contracts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL UNIQUE REFERENCES projects(id) ON DELETE CASCADE,
    client_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    provider_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    contract_terms TEXT NOT NULL,
    payment_terms TEXT,
    delivery_terms TEXT,
    cancellation_policy TEXT,
    client_signed BOOLEAN DEFAULT FALSE,
    client_signed_at TIMESTAMP,
    client_ip_address VARCHAR(50),
    provider_signed BOOLEAN DEFAULT FALSE,
    provider_signed_at TIMESTAMP,
    provider_ip_address VARCHAR(50),
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    contract_start_date TIMESTAMP,
    contract_end_date TIMESTAMP,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- إنشاء الفهارس
CREATE INDEX IF NOT EXISTS idx_contracts_project_id ON contracts(project_id);
CREATE INDEX IF NOT EXISTS idx_contracts_client_id ON contracts(client_id);
CREATE INDEX IF NOT EXISTS idx_contracts_provider_id ON contracts(provider_id);
CREATE INDEX IF NOT EXISTS idx_contracts_status ON contracts(status);
