-- V3: Create calculation_logs table with advanced indexing

CREATE TABLE calculation_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    calculation_type VARCHAR(50) NOT NULL,
    input_data JSONB NOT NULL,
    result_data JSONB NOT NULL,
    total_cost DECIMAL(15,2),
    currency VARCHAR(3) DEFAULT 'IQD',
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- B-Tree Index for user lookups
CREATE INDEX idx_calc_logs_user ON calculation_logs(user_id) WHERE user_id IS NOT NULL;

-- B-Tree Index for calculation type filtering
CREATE INDEX idx_calc_logs_type ON calculation_logs(calculation_type);

-- BRIN Index for time-series queries (logs are sequential)
CREATE INDEX idx_calc_logs_created_brin ON calculation_logs USING BRIN(created_at);

-- GIN Index for searching within input/result JSON
CREATE INDEX idx_calc_logs_input ON calculation_logs USING GIN(input_data);
CREATE INDEX idx_calc_logs_result ON calculation_logs USING GIN(result_data);

-- Composite Index for analytics/reporting queries
CREATE INDEX idx_calc_logs_report ON calculation_logs(calculation_type, created_at DESC, total_cost);
