-- Create job_request_offers table for detailed offer management
-- This allows full negotiation between homeowner and worker

CREATE TABLE IF NOT EXISTS job_request_offers (
    id BIGSERIAL PRIMARY KEY,
    job_request_id BIGINT NOT NULL,
    offered_by VARCHAR(20) NOT NULL CHECK (offered_by IN ('WORKER', 'HOMEOWNER')),
    offered_price DECIMAL(15,2) NOT NULL,
    estimated_duration_days INT,
    proposed_start_date TIMESTAMP,
    offer_notes TEXT,
    payment_terms TEXT,
    warranty_terms TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'COUNTERED', 'EXPIRED')),
    counter_to_offer_id BIGINT,
    rejection_reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    
    CONSTRAINT fk_job_request_offers_job_request FOREIGN KEY (job_request_id) REFERENCES job_requests(id) ON DELETE CASCADE,
    CONSTRAINT fk_job_request_offers_counter FOREIGN KEY (counter_to_offer_id) REFERENCES job_request_offers(id) ON DELETE SET NULL
);

-- Add index for performance
CREATE INDEX IF NOT EXISTS idx_job_request_offers_job_request_id ON job_request_offers(job_request_id);
CREATE INDEX IF NOT EXISTS idx_job_request_offers_status ON job_request_offers(status);
CREATE INDEX IF NOT EXISTS idx_job_request_offers_created_at ON job_request_offers(created_at DESC);

-- Add new columns to job_requests for linking to accepted offer
ALTER TABLE job_requests ADD COLUMN IF NOT EXISTS accepted_offer_id BIGINT;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_job_requests_accepted_offer'
    ) THEN
        ALTER TABLE job_requests
            ADD CONSTRAINT fk_job_requests_accepted_offer
                FOREIGN KEY (accepted_offer_id) REFERENCES job_request_offers(id) ON DELETE SET NULL;
    END IF;
END $$;

-- Add new status for negotiation
-- Note: We'll keep existing statuses and add NEGOTIATING
COMMENT ON COLUMN job_requests.status IS 'PENDING, ACCEPTED, REJECTED, OFFER_SENT, IN_PROGRESS, COMPLETED, CANCELLED, NEGOTIATING';
