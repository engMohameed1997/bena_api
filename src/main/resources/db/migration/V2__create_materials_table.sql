-- V2: Create materials and material_prices tables with advanced indexing

-- Materials master table
CREATE TABLE materials (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) NOT NULL,
    name_ar VARCHAR(100) NOT NULL,
    name_en VARCHAR(100),
    category VARCHAR(50) NOT NULL,
    unit VARCHAR(20) NOT NULL,
    default_price DECIMAL(15,2),
    default_labor_cost DECIMAL(15,2),
    specifications JSONB DEFAULT '{}',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Unique Index on code
CREATE UNIQUE INDEX idx_materials_code ON materials(code);

-- B-Tree Index for category filtering
CREATE INDEX idx_materials_category ON materials(category);

-- GIN Index for JSONB specifications search
CREATE INDEX idx_materials_specs ON materials USING GIN(specifications);

-- Partial Index for active materials
CREATE INDEX idx_materials_active ON materials(category, name_ar) WHERE is_active = TRUE;

-- Material prices history table
CREATE TABLE material_prices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    material_id UUID NOT NULL REFERENCES materials(id) ON DELETE CASCADE,
    price DECIMAL(15,2) NOT NULL,
    labor_cost DECIMAL(15,2),
    currency VARCHAR(3) DEFAULT 'IQD',
    region VARCHAR(50) DEFAULT 'baghdad',
    effective_date DATE NOT NULL,
    source VARCHAR(100),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Composite Index for material + date lookup (most common query)
CREATE INDEX idx_material_prices_lookup ON material_prices(material_id, effective_date DESC);

-- BRIN Index for date range queries
CREATE INDEX idx_material_prices_date_brin ON material_prices USING BRIN(effective_date);

-- Index for region-based queries
CREATE INDEX idx_material_prices_region ON material_prices(region, effective_date DESC);

-- Insert default materials for Iraqi construction
INSERT INTO materials (code, name_ar, name_en, category, unit, default_price, default_labor_cost, specifications) VALUES
-- Bricks
('BRICK_STANDARD', 'طابوق عادي', 'Standard Brick', 'brick', 'piece', 150, 80, '{"bricks_per_m2": 130, "dimensions": "24x12x7.5 cm"}'),
('BRICK_HOLLOW', 'طابوق مفرغ', 'Hollow Brick', 'brick', 'piece', 200, 70, '{"bricks_per_m2": 12, "dimensions": "40x20x20 cm"}'),
('BRICK_THERMAL', 'طابوق عازل حراري', 'Thermal Brick', 'brick', 'piece', 350, 90, '{"bricks_per_m2": 12, "dimensions": "40x20x20 cm"}'),

-- Cement
('CEMENT_PORTLAND', 'سمنت بورتلاندي', 'Portland Cement', 'cement', 'ton', 180000, 50000, '{"bags_per_ton": 20, "kg_per_bag": 50}'),
('CEMENT_RESISTANT', 'سمنت مقاوم', 'Resistant Cement', 'cement', 'ton', 220000, 50000, '{"bags_per_ton": 20, "kg_per_bag": 50}'),

-- Steel
('STEEL_REBAR_10', 'حديد تسليح 10 ملم', 'Rebar 10mm', 'steel', 'ton', 1200000, 150000, '{"diameter_mm": 10, "weight_per_meter": 0.617}'),
('STEEL_REBAR_12', 'حديد تسليح 12 ملم', 'Rebar 12mm', 'steel', 'ton', 1200000, 150000, '{"diameter_mm": 12, "weight_per_meter": 0.888}'),
('STEEL_REBAR_16', 'حديد تسليح 16 ملم', 'Rebar 16mm', 'steel', 'ton', 1200000, 150000, '{"diameter_mm": 16, "weight_per_meter": 1.58}'),

-- Concrete
('CONCRETE_READY', 'خرسانة جاهزة', 'Ready Mix Concrete', 'concrete', 'm3', 180000, 80000, '{"strength": "C25", "slump": "10-15 cm"}'),

-- Slab materials
('HOLLOW_BLOCK', 'بلوك هولو', 'Hollow Block', 'slab', 'piece', 2500, 1000, '{"dimensions": "40x25x20 cm", "blocks_per_m2": 8}'),
('STYROFOAM', 'فلين (ستايروفوم)', 'Styrofoam', 'slab', 'm2', 15000, 5000, '{"thickness_cm": 20}'),

-- Finishing
('TILES_CERAMIC', 'سيراميك', 'Ceramic Tiles', 'finishing', 'm2', 25000, 15000, '{"thickness_mm": 8}'),
('TILES_PORCELAIN', 'بورسلان', 'Porcelain Tiles', 'finishing', 'm2', 45000, 20000, '{"thickness_mm": 10}'),
('PAINT_INTERIOR', 'دهان داخلي', 'Interior Paint', 'finishing', 'm2', 8000, 6000, '{"coats": 2}'),
('PAINT_EXTERIOR', 'دهان خارجي', 'Exterior Paint', 'finishing', 'm2', 12000, 8000, '{"coats": 2}'),

-- Foundation
('GRAVEL', 'حصى', 'Gravel', 'foundation', 'm3', 35000, 20000, '{}'),
('SAND', 'رمل', 'Sand', 'foundation', 'm3', 25000, 15000, '{}'),
('FILL_MATERIAL', 'مواد دفان', 'Fill Material', 'foundation', 'm3', 20000, 25000, '{}');
