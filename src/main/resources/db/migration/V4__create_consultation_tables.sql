-- V4: Create consultation tables for construction advice and information

-- Consultation Categories
CREATE TABLE consultation_categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) NOT NULL UNIQUE,
    name_ar VARCHAR(100) NOT NULL,
    name_en VARCHAR(100),
    description_ar VARCHAR(500),
    description_en VARCHAR(500),
    icon_url TEXT,
    display_order INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Indexes for categories
CREATE INDEX idx_consultation_categories_code ON consultation_categories(code);
CREATE INDEX idx_consultation_categories_active ON consultation_categories(display_order) WHERE is_active = TRUE;

-- Consultation Items
CREATE TABLE consultation_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    category_id UUID NOT NULL REFERENCES consultation_categories(id) ON DELETE CASCADE,
    code VARCHAR(50) NOT NULL,
    name_ar VARCHAR(150) NOT NULL,
    name_en VARCHAR(150),
    description_ar TEXT,
    description_en TEXT,
    price_from DECIMAL(15,2),
    price_to DECIMAL(15,2),
    price_unit VARCHAR(50),
    currency VARCHAR(3) DEFAULT 'IQD',
    advantages JSONB DEFAULT '[]',
    disadvantages JSONB DEFAULT '[]',
    suitable_uses JSONB DEFAULT '[]',
    specifications JSONB DEFAULT '{}',
    tips JSONB DEFAULT '[]',
    image_urls JSONB DEFAULT '[]',
    video_url TEXT,
    rating DECIMAL(2,1),
    view_count BIGINT DEFAULT 0,
    display_order INTEGER DEFAULT 0,
    is_featured BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Indexes for items
CREATE UNIQUE INDEX idx_consultation_items_code ON consultation_items(category_id, code);
CREATE INDEX idx_consultation_items_category ON consultation_items(category_id);
CREATE INDEX idx_consultation_items_featured ON consultation_items(is_featured, display_order) WHERE is_active = TRUE;
CREATE INDEX idx_consultation_items_rating ON consultation_items(rating DESC NULLS LAST) WHERE is_active = TRUE;
CREATE INDEX idx_consultation_items_views ON consultation_items(view_count DESC) WHERE is_active = TRUE;

-- GIN index for JSON search
CREATE INDEX idx_consultation_items_advantages ON consultation_items USING GIN(advantages);
CREATE INDEX idx_consultation_items_specs ON consultation_items USING GIN(specifications);

-- =====================================================
-- Insert Default Categories
-- =====================================================

INSERT INTO consultation_categories (code, name_ar, name_en, description_ar, display_order) VALUES
('soil_types', 'أنواع التربة', 'Soil Types', 'استشارة اختيار نوع التربة المناسب للبناء', 1),
('pre_concrete', 'ما قبل الصب', 'Pre-Concrete', 'استشارات ونصائح قبل صب الخرسانة', 2),
('concrete_types', 'أنواع الصب', 'Concrete Types', 'أنواع الخرسانة والصبات المختلفة', 3),
('slab_types', 'أنواع السقف', 'Slab Types', 'أنواع الأسقف (جسر، رفت، هولو، فلين)', 4),
('steel_types', 'أنواع الحديد', 'Steel Types', 'أنواع حديد التسليح والمواصفات', 5),
('brick_types', 'أنواع الطابوق', 'Brick Types', 'أنواع الطابوق العراقي (جمهوري، سماوة، بلوك)', 6),
('cement_types', 'أنواع السمنت', 'Cement Types', 'أنواع السمنت واستخداماتها', 7),
('plumbing_types', 'أنواع المجاري والسباكة', 'Plumbing Types', 'أنواع أنابيب الماء والصرف الصحي', 8),
('electrical_types', 'أنواع الكهربائيات', 'Electrical Types', 'أنواع الأسلاك والمفاتيح والقواطع', 9),
('tiles_types', 'أنواع الكاشي والسيراميك', 'Tiles Types', 'أنواع البلاط والسيراميك والبورسلان', 10),
('paint_types', 'أنواع الدهان', 'Paint Types', 'أنواع الدهانات الداخلية والخارجية', 11),
('insulation_types', 'أنواع العزل', 'Insulation Types', 'أنواع العزل المائي والحراري', 12),
('foundation_types', 'أنواع الأساسات', 'Foundation Types', 'أنواع القواعد والأساسات', 13),
('doors_windows', 'الأبواب والشبابيك', 'Doors & Windows', 'أنواع الأبواب والشبابيك والألمنيوم', 14),
('facade_types', 'أنواع الواجهات', 'Facade Types', 'أنواع واجهات المباني (حجر، كورك، سيراميك)', 15);

-- =====================================================
-- Insert Brick Types (أنواع الطابوق)
-- =====================================================

INSERT INTO consultation_items (category_id, code, name_ar, name_en, description_ar, price_from, price_to, price_unit, advantages, disadvantages, suitable_uses, specifications, tips, image_urls) VALUES

((SELECT id FROM consultation_categories WHERE code = 'brick_types'),
'brick_jumhuri', 'طابوق جمهوري', 'Jumhuri Brick',
'الطابوق الجمهوري هو الأكثر شيوعاً في العراق، يتميز بجودته العالية ومتانته. يُصنع من الطين المحروق بدرجات حرارة عالية.',
120, 180, 'لكل 1000 طابوقة',
'["متين وقوي", "مقاوم للرطوبة", "عازل حراري جيد", "متوفر بكثرة", "سعر مناسب"]',
'["ثقيل الوزن", "يحتاج عمالة ماهرة", "بطيء في البناء مقارنة بالبلوك"]',
'["الجدران الخارجية", "الجدران الحاملة", "البناء السكني", "البناء التجاري"]',
'{"dimensions": "24×12×7.5 سم", "weight": "3-3.5 كغ", "compressive_strength": "75-100 كغ/سم²", "water_absorption": "15-20%", "bricks_per_m2": 130}',
'["تأكد من صوت الطابوق عند الطرق (يجب أن يكون رنان)", "اختر الطابوق ذو اللون الأحمر المتجانس", "تجنب الطابوق المتشقق أو المكسور"]',
'[]'),

((SELECT id FROM consultation_categories WHERE code = 'brick_types'),
'brick_samawa', 'طابوق سماوة', 'Samawa Brick',
'طابوق سماوة يُصنع في محافظة المثنى، معروف بجودته العالية ولونه المميز. يُعتبر من أفضل أنواع الطابوق في العراق.',
150, 220, 'لكل 1000 طابوقة',
'["جودة عالية جداً", "لون مميز وجميل", "مقاومة عالية للضغط", "عمر افتراضي طويل"]',
'["سعره أعلى من الجمهوري", "قد لا يتوفر في كل المناطق"]',
'["الفلل الفاخرة", "البناء عالي الجودة", "الواجهات المكشوفة"]',
'{"dimensions": "24×12×7.5 سم", "weight": "3.2-3.8 كغ", "compressive_strength": "100-150 كغ/سم²", "water_absorption": "12-15%", "bricks_per_m2": 130}',
'["يُفضل للبناء الفاخر", "مناسب للواجهات بدون لبخ", "اطلب شهادة الجودة من المصنع"]',
'[]'),

((SELECT id FROM consultation_categories WHERE code = 'brick_types'),
'block_concrete', 'بلوك خرساني', 'Concrete Block',
'البلوك الخرساني (الثرمستون) خفيف الوزن وسريع البناء. مصنوع من الخرسانة الخفيفة.',
2000, 3500, 'لكل 1000 بلوكة',
'["خفيف الوزن", "سريع البناء", "عازل حراري ممتاز", "يقلل الحمل على الهيكل", "اقتصادي"]',
'["أقل متانة من الطابوق", "يحتاج للبخ", "قد يتشقق مع الوقت"]',
'["الجدران الداخلية", "الفواصل", "البناء السريع", "الطوابق العليا"]',
'{"dimensions": "40×20×20 سم", "weight": "10-12 كغ", "compressive_strength": "25-35 كغ/سم²", "blocks_per_m2": 12}',
'["مناسب للجدران غير الحاملة", "استخدم غراء البلوك بدل المونة", "تأكد من جفافه قبل البناء"]',
'[]'),

((SELECT id FROM consultation_categories WHERE code = 'brick_types'),
'block_thermal', 'بلوك عازل حراري', 'Thermal Block',
'بلوك عازل حراري مصنوع من مواد خاصة توفر عزل حراري ممتاز. مثالي للمناطق الحارة.',
3500, 5000, 'لكل 1000 بلوكة',
'["عزل حراري ممتاز", "يوفر في فاتورة الكهرباء", "خفيف الوزن", "صديق للبيئة"]',
'["سعره مرتفع", "يحتاج تركيب متخصص"]',
'["الجدران الخارجية", "المناطق الحارة", "البناء الموفر للطاقة"]',
'{"dimensions": "40×20×20 سم", "weight": "8-10 كغ", "thermal_conductivity": "0.12 W/mK", "blocks_per_m2": 12}',
'["استثمار جيد على المدى الطويل", "يقلل استهلاك المكيفات بنسبة 30-40%"]',
'[]'),

((SELECT id FROM consultation_categories WHERE code = 'brick_types'),
'brick_faham', 'طابوق فحم (أسود)', 'Charcoal Brick',
'طابوق محروق بدرجة حرارة عالية جداً، لونه أسود داكن. يُستخدم للأغراض الخاصة.',
200, 300, 'لكل 1000 طابوقة',
'["صلابة عالية جداً", "مقاوم للماء", "عمر طويل"]',
'["سعره مرتفع", "ثقيل جداً", "صعب القص"]',
'["الأساسات", "المناطق الرطبة", "السراديب"]',
'{"dimensions": "24×12×7.5 سم", "weight": "3.5-4 كغ", "compressive_strength": "150+ كغ/سم²"}',
'["مثالي للأماكن المعرضة للرطوبة", "يُستخدم في الطابق السفلي"]',
'[]');

-- =====================================================
-- Insert Concrete/Slab Types (أنواع الصب والسقف)
-- =====================================================

INSERT INTO consultation_items (category_id, code, name_ar, name_en, description_ar, price_from, price_to, price_unit, advantages, disadvantages, suitable_uses, specifications, tips) VALUES

((SELECT id FROM consultation_categories WHERE code = 'slab_types'),
'slab_hollow', 'سقف هولو بلوك', 'Hollow Block Slab',
'السقف الهولو هو الأكثر شيوعاً في العراق. يتكون من بلوكات مفرغة مع أعصاب خرسانية.',
80000, 120000, 'لكل متر مربع',
'["اقتصادي", "خفيف الوزن", "عزل حراري جيد", "سهل التنفيذ"]',
'["يحتاج دعم مؤقت", "محدود بالفتحات الكبيرة"]',
'["البناء السكني", "الفتحات حتى 5 متر", "الطوابق المتعددة"]',
'{"thickness": "25-30 سم", "block_size": "40×25×20 سم", "blocks_per_m2": 8, "concrete_volume": "0.08 م³/م²", "steel_ratio": "80 كغ/م³"}',
'["تأكد من رص البلوك بشكل صحيح", "لا تمشي على البلوك قبل الصب", "اسقِ السقف لمدة 7 أيام"]'),

((SELECT id FROM consultation_categories WHERE code = 'slab_types'),
'slab_raft', 'سقف رفت (صلب)', 'Raft/Solid Slab',
'السقف الرفت أو الصلب هو سقف خرساني متجانس بدون بلوك. يُستخدم للفتحات الكبيرة.',
120000, 180000, 'لكل متر مربع',
'["قوة تحمل عالية", "مناسب للفتحات الكبيرة", "مرونة في التصميم"]',
'["ثقيل الوزن", "يحتاج حديد أكثر", "تكلفة أعلى"]',
'["الفتحات الكبيرة +6 متر", "المباني التجارية", "الكراجات"]',
'{"thickness": "15-25 سم", "concrete_volume": "0.15-0.25 م³/م²", "steel_ratio": "100-120 كغ/م³"}',
'["يحتاج تصميم إنشائي دقيق", "استخدم خرسانة جاهزة عالية الجودة"]'),

((SELECT id FROM consultation_categories WHERE code = 'slab_types'),
'slab_joist', 'سقف جسر', 'Joist Slab',
'سقف الجسر يتكون من جسور خرسانية مسبقة الصنع مع بلوك أو طابوق.',
70000, 100000, 'لكل متر مربع',
'["سريع التنفيذ", "اقتصادي", "لا يحتاج طوبار كثير"]',
'["محدود بالفتحات", "يحتاج رافعة للجسور"]',
'["البناء السكني البسيط", "الفتحات حتى 4 متر"]',
'{"joist_spacing": "50-60 سم", "joist_height": "20-25 سم"}',
'["تأكد من جودة الجسور المسبقة", "اربط الجسور جيداً بالجدران"]'),

((SELECT id FROM consultation_categories WHERE code = 'slab_types'),
'slab_styrofoam', 'سقف فلين (ستايروفوم)', 'Styrofoam Slab',
'سقف الفلين يستخدم قوالب ستايروفوم بدل البلوك. خفيف جداً وعازل ممتاز.',
90000, 140000, 'لكل متر مربع',
'["خفيف جداً", "عزل حراري ممتاز", "سريع التنفيذ", "يقلل الحمل على الهيكل"]',
'["يحتاج حماية من الحريق", "سعره أعلى من الهولو"]',
'["الطوابق العليا", "المناطق الحارة", "البناء الخفيف"]',
'{"thickness": "25-30 سم", "styrofoam_density": "15-20 كغ/م³"}',
'["غطِ الفلين بطبقة حماية", "استخدم فلين عالي الكثافة"]');

-- =====================================================
-- Insert Steel Types (أنواع الحديد)
-- =====================================================

INSERT INTO consultation_items (category_id, code, name_ar, name_en, description_ar, price_from, price_to, price_unit, advantages, disadvantages, suitable_uses, specifications, tips) VALUES

((SELECT id FROM consultation_categories WHERE code = 'steel_types'),
'steel_turkish', 'حديد تركي', 'Turkish Steel',
'الحديد التركي من أكثر الأنواع شيوعاً في العراق. جودة جيدة وسعر مناسب.',
1100000, 1300000, 'لكل طن',
'["جودة جيدة", "سعر مناسب", "متوفر بكثرة", "مطابق للمواصفات"]',
'["قد يتفاوت بين الشركات"]',
'["جميع أنواع البناء", "الأساسات", "الأسقف", "الأعمدة"]',
'{"grades": "B420C, B500C", "yield_strength": "420-500 MPa", "available_diameters": "8, 10, 12, 14, 16, 18, 20, 22, 25, 28, 32 mm"}',
'["اطلب شهادة المطابقة", "تأكد من علامة المصنع على القضبان", "خزّنه بعيداً عن الرطوبة"]'),

((SELECT id FROM consultation_categories WHERE code = 'steel_types'),
'steel_ukrainian', 'حديد أوكراني', 'Ukrainian Steel',
'حديد أوكراني عالي الجودة، يُستخدم في المشاريع الكبيرة.',
1150000, 1350000, 'لكل طن',
'["جودة عالية", "مقاومة شد ممتازة", "مطابق للمواصفات الأوروبية"]',
'["سعره أعلى قليلاً"]',
'["المشاريع الكبيرة", "الجسور", "المباني العالية"]',
'{"grades": "B500B, B500C", "yield_strength": "500 MPa"}',
'["مناسب للمشاريع التي تتطلب جودة عالية"]'),

((SELECT id FROM consultation_categories WHERE code = 'steel_types'),
'steel_iranian', 'حديد إيراني', 'Iranian Steel',
'حديد إيراني متوفر بأسعار تنافسية.',
1000000, 1200000, 'لكل طن',
'["سعر اقتصادي", "متوفر"]',
'["جودة متفاوتة", "تحقق من المصدر"]',
'["البناء السكني البسيط"]',
'{"grades": "متنوعة", "yield_strength": "400-500 MPa"}',
'["تأكد من مصدر الحديد", "اطلب فحص مختبري"]'),

((SELECT id FROM consultation_categories WHERE code = 'steel_types'),
'steel_local', 'حديد محلي (عراقي)', 'Local Iraqi Steel',
'حديد مصنّع محلياً في العراق.',
950000, 1150000, 'لكل طن',
'["دعم الصناعة المحلية", "سعر مناسب", "سهولة التوفر"]',
'["الجودة تختلف حسب المصنع"]',
'["البناء السكني", "الأعمال الصغيرة"]',
'{"grades": "متنوعة"}',
'["اختر مصانع معروفة", "اطلب شهادة الجودة"]');

-- =====================================================
-- Insert Plumbing Types (أنواع المجاري والسباكة)
-- =====================================================

INSERT INTO consultation_items (category_id, code, name_ar, name_en, description_ar, price_from, price_to, price_unit, advantages, disadvantages, suitable_uses, specifications, tips) VALUES

((SELECT id FROM consultation_categories WHERE code = 'plumbing_types'),
'pipe_pvc', 'أنابيب PVC', 'PVC Pipes',
'أنابيب بلاستيكية للصرف الصحي، الأكثر استخداماً في العراق.',
3000, 8000, 'لكل متر (حسب القطر)',
'["سعر اقتصادي", "خفيفة الوزن", "سهلة التركيب", "مقاومة للتآكل", "عمر طويل"]',
'["تتأثر بالحرارة العالية", "قد تتكسر بالصدمات"]',
'["الصرف الصحي", "تصريف المطر", "المجاري الخارجية"]',
'{"diameters": "50, 75, 110, 160, 200, 250, 315 mm", "pressure_rating": "PN4-PN10", "material": "PVC-U"}',
'["استخدم غراء PVC الأصلي", "تجنب التعرض المباشر للشمس", "ادفن الأنابيب بعمق 50 سم على الأقل"]'),

((SELECT id FROM consultation_categories WHERE code = 'plumbing_types'),
'pipe_ppr', 'أنابيب PPR', 'PPR Pipes',
'أنابيب بلاستيكية للماء الساخن والبارد.',
4000, 12000, 'لكل متر (حسب القطر)',
'["مقاومة للحرارة", "لا تصدأ", "صحية للشرب", "عمر 50+ سنة"]',
'["تحتاج لحام حراري", "سعرها أعلى من PVC"]',
'["شبكة الماء الداخلية", "الماء الساخن", "التدفئة"]',
'{"diameters": "20, 25, 32, 40, 50, 63 mm", "max_temp": "95°C", "pressure_rating": "PN20-PN25"}',
'["استخدم لحام PPR أصلي", "اترك مسافة للتمدد", "افحص اللحام قبل الدفن"]'),

((SELECT id FROM consultation_categories WHERE code = 'plumbing_types'),
'pipe_hdpe', 'أنابيب HDPE', 'HDPE Pipes',
'أنابيب بولي إيثيلين عالي الكثافة للشبكات الخارجية.',
5000, 15000, 'لكل متر (حسب القطر)',
'["مرونة عالية", "مقاومة للضغط", "لا تتأثر بالتربة", "عمر 100 سنة"]',
'["تحتاج معدات لحام خاصة", "سعرها مرتفع"]',
'["شبكات المياه الرئيسية", "الري", "نقل الغاز"]',
'{"diameters": "20-1200 mm", "pressure_rating": "PN6-PN16"}',
'["مثالية للشبكات الطويلة", "تتحمل حركة التربة"]'),

((SELECT id FROM consultation_categories WHERE code = 'plumbing_types'),
'pipe_cast_iron', 'أنابيب حديد الزهر', 'Cast Iron Pipes',
'أنابيب حديدية للصرف الصحي الثقيل.',
15000, 40000, 'لكل متر (حسب القطر)',
'["قوة تحمل عالية", "عازلة للصوت", "مقاومة للحريق"]',
'["ثقيلة جداً", "تصدأ مع الوقت", "سعرها مرتفع"]',
'["المباني العالية", "المستشفيات", "الفنادق"]',
'{"diameters": "50-300 mm"}',
'["تُستخدم في المباني الفاخرة", "تحتاج صيانة دورية"]'),

((SELECT id FROM consultation_categories WHERE code = 'plumbing_types'),
'septic_concrete', 'بيارة خرسانية', 'Concrete Septic Tank',
'خزان صرف صحي خرساني للمناطق بدون شبكة مجاري.',
500000, 1500000, 'لكل بيارة (حسب الحجم)',
'["متينة", "سعة كبيرة", "عمر طويل"]',
'["تحتاج تفريغ دوري", "قد تتسرب مع الوقت"]',
'["المناطق الريفية", "البيوت المستقلة"]',
'{"volumes": "3, 5, 8, 10, 15 م³"}',
'["اختر حجم مناسب لعدد السكان", "افرغها كل 6-12 شهر"]'),

((SELECT id FROM consultation_categories WHERE code = 'plumbing_types'),
'septic_plastic', 'بيارة بلاستيكية', 'Plastic Septic Tank',
'خزان صرف صحي بلاستيكي جاهز.',
800000, 2500000, 'لكل بيارة (حسب الحجم)',
'["لا تتسرب", "سهلة التركيب", "خفيفة", "صديقة للبيئة"]',
'["سعرها أعلى", "تحتاج تثبيت جيد"]',
'["البناء الحديث", "المناطق ذات المياه الجوفية العالية"]',
'{"volumes": "1, 2, 3, 5 م³", "material": "HDPE"}',
'["ثبّتها جيداً لمنع الطفو", "اختر نوع معالج للأشعة"]');

-- =====================================================
-- Insert Soil Types (أنواع التربة)
-- =====================================================

INSERT INTO consultation_items (category_id, code, name_ar, name_en, description_ar, price_from, price_to, price_unit, advantages, disadvantages, suitable_uses, specifications, tips) VALUES

((SELECT id FROM consultation_categories WHERE code = 'soil_types'),
'soil_rocky', 'تربة صخرية', 'Rocky Soil',
'تربة صلبة تحتوي على صخور. أفضل أنواع التربة للبناء.',
NULL, NULL, NULL,
'["قدرة تحمل عالية جداً", "لا تحتاج تحسين", "مستقرة"]',
'["صعوبة الحفر", "تكلفة حفر عالية"]',
'["المباني العالية", "الجسور", "المنشآت الثقيلة"]',
'{"bearing_capacity": "10+ كغ/سم²", "settlement": "ضئيل جداً"}',
'["أفضل تربة للبناء", "قد تحتاج معدات حفر خاصة"]'),

((SELECT id FROM consultation_categories WHERE code = 'soil_types'),
'soil_gravel', 'تربة حصوية', 'Gravel Soil',
'تربة تحتوي على حصى ورمل خشن. جيدة للبناء.',
NULL, NULL, NULL,
'["قدرة تحمل جيدة", "تصريف ماء ممتاز", "سهلة الحفر"]',
'["قد تحتاج دمج"]',
'["معظم أنواع البناء", "الأساسات العادية"]',
'{"bearing_capacity": "3-5 كغ/سم²", "drainage": "ممتاز"}',
'["تربة مثالية للبناء السكني"]'),

((SELECT id FROM consultation_categories WHERE code = 'soil_types'),
'soil_sandy', 'تربة رملية', 'Sandy Soil',
'تربة تحتوي على نسبة عالية من الرمل.',
NULL, NULL, NULL,
'["سهلة الحفر", "تصريف جيد"]',
'["قدرة تحمل متوسطة", "قد تحتاج تحسين", "غير مستقرة مع الماء"]',
'["البناء الخفيف", "بعد التحسين"]',
'{"bearing_capacity": "1.5-2.5 كغ/سم²"}',
'["قد تحتاج حقن أو دمج", "تجنب البناء بدون فحص"]'),

((SELECT id FROM consultation_categories WHERE code = 'soil_types'),
'soil_clay', 'تربة طينية', 'Clay Soil',
'تربة تحتوي على نسبة عالية من الطين. تتأثر بالماء.',
NULL, NULL, NULL,
'["متماسكة"]',
'["تنتفخ مع الماء", "تنكمش مع الجفاف", "قدرة تحمل ضعيفة", "بطيئة التصريف"]',
'["تحتاج معالجة خاصة"]',
'{"bearing_capacity": "1-2 كغ/سم²", "swelling": "عالي"}',
'["خطيرة جداً للبناء بدون معالجة", "استشر مهندس جيوتقني", "قد تحتاج أساسات عميقة"]'),

((SELECT id FROM consultation_categories WHERE code = 'soil_types'),
'soil_fill', 'تربة ردم', 'Fill Soil',
'تربة منقولة أو مردومة. غير مستقرة.',
NULL, NULL, NULL,
'[]',
'["غير مستقرة", "قدرة تحمل ضعيفة", "تهبط مع الوقت"]',
'["يجب إزالتها أو معالجتها"]',
'{"bearing_capacity": "غير موثوقة"}',
'["لا تبني على تربة ردم مباشرة", "يجب الحفر حتى التربة الأصلية أو الدمج الجيد"]');

-- =====================================================
-- Insert Pre-Concrete Advice (نصائح ما قبل الصب)
-- =====================================================

INSERT INTO consultation_items (category_id, code, name_ar, name_en, description_ar, advantages, tips) VALUES

((SELECT id FROM consultation_categories WHERE code = 'pre_concrete'),
'pre_check_steel', 'فحص الحديد قبل الصب', 'Steel Inspection Before Pouring',
'خطوات فحص حديد التسليح قبل صب الخرسانة للتأكد من الجودة والتنفيذ الصحيح.',
'["ضمان جودة البناء", "تجنب المشاكل الإنشائية", "توفير التكاليف"]',
'["تأكد من أقطار الحديد حسب المخطط", "افحص المسافات بين القضبان", "تأكد من الغطاء الخرساني (5-7 سم)", "افحص الربط والتراكب", "تأكد من نظافة الحديد من الصدأ", "افحص الكراسي والفواصل", "وثّق بالصور قبل الصب"]'),

((SELECT id FROM consultation_categories WHERE code = 'pre_concrete'),
'pre_check_formwork', 'فحص القالب (الطوبار)', 'Formwork Inspection',
'فحص القالب الخشبي أو المعدني قبل الصب.',
'["منع تسرب الخرسانة", "ضمان الأبعاد الصحيحة", "سطح أملس"]',
'["تأكد من إحكام القالب", "افحص الدعامات والتثبيت", "ادهن القالب بالزيت", "تأكد من المناسيب والأبعاد", "افحص فتحات الكهرباء والسباكة"]'),

((SELECT id FROM consultation_categories WHERE code = 'pre_concrete'),
'pre_check_concrete', 'فحص الخرسانة', 'Concrete Inspection',
'كيفية فحص الخرسانة الجاهزة عند وصولها.',
'["ضمان جودة الخرسانة", "تجنب المشاكل"]',
'["اطلب فاتورة توضح نوع الخرسانة", "افحص قوام الخرسانة (Slump Test)", "لا تقبل خرسانة مضاف لها ماء", "خذ عينات مكعبات للفحص", "تأكد من وقت الخلط (أقل من 90 دقيقة)"]'),

((SELECT id FROM consultation_categories WHERE code = 'pre_concrete'),
'pre_weather', 'الطقس المناسب للصب', 'Weather Conditions for Pouring',
'الظروف الجوية المناسبة لصب الخرسانة.',
'["جودة خرسانة أفضل", "تجنب التشققات"]',
'["تجنب الصب في الحر الشديد (+40°C)", "تجنب الصب في البرد الشديد (-5°C)", "تجنب الصب أثناء المطر", "أفضل وقت: الصباح الباكر صيفاً", "غطِ الخرسانة بعد الصب", "اسقِ الخرسانة لمدة 7 أيام"]');
