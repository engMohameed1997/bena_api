-- Create advertisements tables

CREATE TABLE IF NOT EXISTS advertisements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(200) NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    target_type VARCHAR(20) NOT NULL,
    target_value VARCHAR(500) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    priority INTEGER NOT NULL DEFAULT 0,
    start_at TIMESTAMPTZ NULL,
    end_at TIMESTAMPTZ NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_advertisements_target_type
        CHECK (target_type IN ('SCREEN', 'URL', 'SERVICE')),

    CONSTRAINT chk_advertisements_time_window
        CHECK (start_at IS NULL OR end_at IS NULL OR end_at >= start_at)
);

CREATE TABLE IF NOT EXISTS advertisement_sections (
    advertisement_id UUID NOT NULL REFERENCES advertisements(id) ON DELETE CASCADE,
    section VARCHAR(30) NOT NULL,
    PRIMARY KEY (advertisement_id, section),

    CONSTRAINT chk_advertisement_sections_section
        CHECK (section IN ('HOME', 'BUILD_STEPS', 'ESCROW'))
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_ads_active ON advertisements(active);
CREATE INDEX IF NOT EXISTS idx_ads_priority ON advertisements(priority);
CREATE INDEX IF NOT EXISTS idx_ads_start_at ON advertisements(start_at);
CREATE INDEX IF NOT EXISTS idx_ads_end_at ON advertisements(end_at);
CREATE INDEX IF NOT EXISTS idx_ads_created_at ON advertisements(created_at);

CREATE INDEX IF NOT EXISTS idx_ad_sections_section ON advertisement_sections(section);

-- Optimized common query path: active ads ordered by priority
CREATE INDEX IF NOT EXISTS idx_ads_active_priority ON advertisements(priority) WHERE active = TRUE;
