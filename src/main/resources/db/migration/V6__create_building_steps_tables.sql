-- جداول خطوات بناء المنزل
 
-- جدول تصنيفات الخطوات
CREATE TABLE IF NOT EXISTS step_categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    icon_name VARCHAR(100),
    color_hex VARCHAR(10),
    category_order INT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
 
-- جدول الخطوات الرئيسية
CREATE TABLE IF NOT EXISTS building_steps (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    step_order INT,
    category_id BIGINT,
    icon_name VARCHAR(100),
    estimated_duration VARCHAR(100),
    estimated_cost_percentage DOUBLE PRECISION,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES step_categories(id)
);
 
-- جدول الخطوات الفرعية
CREATE TABLE IF NOT EXISTS sub_steps (
    id BIGSERIAL PRIMARY KEY,
    building_step_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    sub_step_order INT,
    tips TEXT,
    warnings TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (building_step_id) REFERENCES building_steps(id)
);
 
-- جدول الوسائط (صور وفيديوهات)
CREATE TABLE IF NOT EXISTS step_media (
    id BIGSERIAL PRIMARY KEY,
    building_step_id BIGINT NOT NULL,
    sub_step_id BIGINT,
    media_type VARCHAR(20) NOT NULL CHECK (media_type IN ('IMAGE', 'VIDEO', 'DIAGRAM')),
    url VARCHAR(500) NOT NULL,
    thumbnail_url VARCHAR(500),
    title VARCHAR(255),
    caption TEXT,
    media_order INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (building_step_id) REFERENCES building_steps(id),
    FOREIGN KEY (sub_step_id) REFERENCES sub_steps(id)
);

-- =====================================================
-- إدخال البيانات الأولية
-- =====================================================

-- تصنيفات الخطوات
INSERT INTO step_categories (name, description, icon_name, color_hex, category_order)
SELECT v.name, v.description, v.icon_name, v.color_hex, v.category_order
FROM (VALUES
('التخطيط والتصميم', 'مرحلة التخطيط والتصميم المعماري', 'design_services', '#2196F3', 1),
('الأساسات', 'مرحلة حفر وصب الأساسات', 'foundation', '#795548', 2),
('الهيكل الإنشائي', 'بناء الهيكل والأعمدة والسقوف', 'apartment', '#607D8B', 3),
('البناء والجدران', 'بناء الجدران الداخلية والخارجية', 'grid_view', '#FF9800', 4),
('التمديدات', 'الكهرباء والسباكة والتكييف', 'electrical_services', '#FFC107', 5),
('التشطيبات', 'اللياسة والدهان والأرضيات', 'format_paint', '#9C27B0', 6),
('التشطيبات النهائية', 'الأبواب والنوافذ والديكور', 'door_front', '#4CAF50', 7)
) AS v(name, description, icon_name, color_hex, category_order)
WHERE NOT EXISTS (
    SELECT 1
    FROM step_categories sc
    WHERE sc.name = v.name
);

-- =====================================================
-- خطوات التخطيط والتصميم
-- =====================================================
WITH category AS (
    SELECT id AS category_id
    FROM step_categories
    WHERE name = 'التخطيط والتصميم'
)
INSERT INTO building_steps (title, description, step_order, category_id, icon_name, estimated_duration, estimated_cost_percentage)
SELECT v.title, v.description, v.step_order, category.category_id, v.icon_name, v.estimated_duration, v.estimated_cost_percentage
FROM category
CROSS JOIN (VALUES
('شراء الأرض', 'اختيار وشراء قطعة الأرض المناسبة للبناء مع التأكد من الأوراق القانونية', 1, 'landscape', '1-4 أسابيع', 0),
('التصميم المعماري', 'إعداد المخططات المعمارية مع مهندس معماري متخصص', 2, 'architecture', '2-4 أسابيع', 3),
('الحصول على التراخيص', 'استخراج رخصة البناء من البلدية والجهات المختصة', 3, 'description', '2-6 أسابيع', 1),
('اختيار المقاول', 'البحث عن مقاول موثوق والاتفاق على العقد', 4, 'engineering', '1-2 أسابيع', 0)
) AS v(title, description, step_order, icon_name, estimated_duration, estimated_cost_percentage)
WHERE NOT EXISTS (
    SELECT 1
    FROM building_steps bs
    WHERE bs.title = v.title
      AND bs.category_id = category.category_id
);

-- =====================================================
-- خطوات الأساسات
-- =====================================================
WITH category AS (
    SELECT id AS category_id
    FROM step_categories
    WHERE name = 'الأساسات'
)
INSERT INTO building_steps (title, description, step_order, category_id, icon_name, estimated_duration, estimated_cost_percentage)
SELECT v.title, v.description, v.step_order, category.category_id, v.icon_name, v.estimated_duration, v.estimated_cost_percentage
FROM category
CROSS JOIN (VALUES
('تنظيف وتسوية الأرض', 'إزالة العوائق وتسوية الأرض وتحديد مناسيب البناء', 1, 'terrain', '2-3 أيام', 1),
('حفر الأساسات', 'حفر خنادق الأساسات حسب المخططات الإنشائية', 2, 'construction', '3-7 أيام', 2),
('صب الخرسانة العادية', 'صب طبقة النظافة تحت الأساسات', 3, 'layers', '1-2 أيام', 2),
('تسليح الأساسات', 'تركيب حديد التسليح للقواعد والرقاب', 4, 'grid_on', '3-5 أيام', 5),
('صب القواعد', 'صب الخرسانة المسلحة للقواعد', 5, 'view_in_ar', '1-2 أيام', 4),
('بناء الرقاب', 'بناء رقاب الأعمدة فوق القواعد', 6, 'view_column', '2-3 أيام', 2),
('الردم والدك', 'ردم التربة حول الأساسات ودكها جيداً', 7, 'compress', '2-3 أيام', 1)
) AS v(title, description, step_order, icon_name, estimated_duration, estimated_cost_percentage)
WHERE NOT EXISTS (
    SELECT 1
    FROM building_steps bs
    WHERE bs.title = v.title
      AND bs.category_id = category.category_id
);

-- =====================================================
-- خطوات الهيكل الإنشائي
-- =====================================================
WITH category AS (
    SELECT id AS category_id
    FROM step_categories
    WHERE name = 'الهيكل الإنشائي'
)
INSERT INTO building_steps (title, description, step_order, category_id, icon_name, estimated_duration, estimated_cost_percentage)
SELECT v.title, v.description, v.step_order, category.category_id, v.icon_name, v.estimated_duration, v.estimated_cost_percentage
FROM category
CROSS JOIN (VALUES
('صب الميدة', 'صب الجسور الأرضية (الميدة) التي تربط الأعمدة', 1, 'linear_scale', '2-3 أيام', 3),
('بناء الأعمدة', 'تسليح وصب أعمدة الطابق الأرضي', 2, 'view_week', '5-7 أيام', 6),
('تركيب الشدة الخشبية', 'تركيب القوالب الخشبية للسقف', 3, 'carpenter', '3-5 أيام', 2),
('تسليح السقف', 'تركيب حديد تسليح السقف والجسور', 4, 'grid_4x4', '5-7 أيام', 7),
('صب السقف', 'صب خرسانة السقف مع المعالجة', 5, 'roofing', '1 يوم + 7 أيام معالجة', 5),
('فك الشدة', 'إزالة القوالب الخشبية بعد تصلب الخرسانة', 6, 'handyman', '2-3 أيام', 0)
) AS v(title, description, step_order, icon_name, estimated_duration, estimated_cost_percentage)
WHERE NOT EXISTS (
    SELECT 1
    FROM building_steps bs
    WHERE bs.title = v.title
      AND bs.category_id = category.category_id
);

-- =====================================================
-- خطوات البناء والجدران
-- =====================================================
WITH category AS (
    SELECT id AS category_id
    FROM step_categories
    WHERE name = 'البناء والجدران'
)
INSERT INTO building_steps (title, description, step_order, category_id, icon_name, estimated_duration, estimated_cost_percentage)
SELECT v.title, v.description, v.step_order, category.category_id, v.icon_name, v.estimated_duration, v.estimated_cost_percentage
FROM category
CROSS JOIN (VALUES
('بناء الجدران الخارجية', 'بناء الجدران الخارجية بالطابوق أو البلوك', 1, 'grid_view', '1-2 أسابيع', 5),
('بناء الجدران الداخلية', 'بناء الفواصل والجدران الداخلية', 2, 'border_inner', '1-2 أسابيع', 3),
('تركيب الحلوق', 'تركيب إطارات الأبواب والنوافذ', 3, 'door_sliding', '3-5 أيام', 2),
('العزل الحراري', 'تركيب مواد العزل الحراري للجدران الخارجية', 4, 'thermostat', '3-5 أيام', 2)
) AS v(title, description, step_order, icon_name, estimated_duration, estimated_cost_percentage)
WHERE NOT EXISTS (
    SELECT 1
    FROM building_steps bs
    WHERE bs.title = v.title
      AND bs.category_id = category.category_id
);

-- =====================================================
-- خطوات التمديدات
-- =====================================================
WITH category AS (
    SELECT id AS category_id
    FROM step_categories
    WHERE name = 'التمديدات'
)
INSERT INTO building_steps (title, description, step_order, category_id, icon_name, estimated_duration, estimated_cost_percentage)
SELECT v.title, v.description, v.step_order, category.category_id, v.icon_name, v.estimated_duration, v.estimated_cost_percentage
FROM category
CROSS JOIN (VALUES
('تمديدات الصرف الصحي', 'تمديد أنابيب الصرف الصحي الرئيسية والفرعية', 1, 'plumbing', '3-5 أيام', 3),
('تمديدات المياه', 'تمديد أنابيب المياه الباردة والساخنة', 2, 'water_drop', '3-5 أيام', 3),
('التمديدات الكهربائية', 'تمديد الأسلاك والمواسير الكهربائية', 3, 'electrical_services', '1-2 أسابيع', 4),
('تمديدات التكييف', 'تمديد مواسير ومجاري التكييف', 4, 'ac_unit', '3-5 أيام', 2),
('تمديدات الغاز', 'تمديد أنابيب الغاز المركزي إن وجد', 5, 'local_fire_department', '2-3 أيام', 1)
) AS v(title, description, step_order, icon_name, estimated_duration, estimated_cost_percentage)
WHERE NOT EXISTS (
    SELECT 1
    FROM building_steps bs
    WHERE bs.title = v.title
      AND bs.category_id = category.category_id
);

-- =====================================================
-- خطوات التشطيبات
-- =====================================================
WITH category AS (
    SELECT id AS category_id
    FROM step_categories
    WHERE name = 'التشطيبات'
)
INSERT INTO building_steps (title, description, step_order, category_id, icon_name, estimated_duration, estimated_cost_percentage)
SELECT v.title, v.description, v.step_order, category.category_id, v.icon_name, v.estimated_duration, v.estimated_cost_percentage
FROM category
CROSS JOIN (VALUES
('اللياسة الداخلية', 'لياسة الجدران الداخلية بالجبس أو الإسمنت', 1, 'format_paint', '2-3 أسابيع', 4),
('اللياسة الخارجية', 'لياسة الواجهات الخارجية', 2, 'home_work', '1-2 أسابيع', 3),
('العزل المائي', 'عزل الحمامات والأسطح ضد الماء', 3, 'water_damage', '3-5 أيام', 2),
('تركيب السيراميك', 'تركيب بلاط الأرضيات والجدران', 4, 'grid_on', '2-3 أسابيع', 5),
('الدهانات', 'دهان الجدران الداخلية والخارجية', 5, 'brush', '2-3 أسابيع', 3),
('تركيب الجبس', 'تركيب أسقف وديكورات الجبس', 6, 'crop_square', '1-2 أسابيع', 3)
) AS v(title, description, step_order, icon_name, estimated_duration, estimated_cost_percentage)
WHERE NOT EXISTS (
    SELECT 1
    FROM building_steps bs
    WHERE bs.title = v.title
      AND bs.category_id = category.category_id
);

-- =====================================================
-- خطوات التشطيبات النهائية
-- =====================================================
WITH category AS (
    SELECT id AS category_id
    FROM step_categories
    WHERE name = 'التشطيبات النهائية'
)
INSERT INTO building_steps (title, description, step_order, category_id, icon_name, estimated_duration, estimated_cost_percentage)
SELECT v.title, v.description, v.step_order, category.category_id, v.icon_name, v.estimated_duration, v.estimated_cost_percentage
FROM category
CROSS JOIN (VALUES
('تركيب الأبواب', 'تركيب الأبواب الداخلية والخارجية', 1, 'door_front', '3-5 أيام', 3),
('تركيب النوافذ', 'تركيب النوافذ الألمنيوم أو UPVC', 2, 'window', '3-5 أيام', 4),
('تركيب الأدوات الصحية', 'تركيب المغاسل والمراحيض والخلاطات', 3, 'bathroom', '3-5 أيام', 3),
('تركيب الأجهزة الكهربائية', 'تركيب المفاتيح والأفياش والإضاءة', 4, 'lightbulb', '3-5 أيام', 2),
('تركيب المطبخ', 'تركيب خزائن ورخام المطبخ', 5, 'kitchen', '3-5 أيام', 4),
('الأعمال الخارجية', 'تنسيق الحديقة والأسوار والمواقف', 6, 'yard', '1-2 أسابيع', 3),
('التنظيف والتسليم', 'تنظيف الموقع والفحص النهائي', 7, 'cleaning_services', '2-3 أيام', 0)
) AS v(title, description, step_order, icon_name, estimated_duration, estimated_cost_percentage)
WHERE NOT EXISTS (
    SELECT 1
    FROM building_steps bs
    WHERE bs.title = v.title
      AND bs.category_id = category.category_id
);

-- =====================================================
-- الخطوات الفرعية لبعض الخطوات الرئيسية
-- =====================================================

-- خطوات فرعية لـ "التصميم المعماري"
WITH step AS (
    SELECT bs.id AS building_step_id
    FROM building_steps bs
    JOIN step_categories sc ON sc.id = bs.category_id
    WHERE bs.title = 'التصميم المعماري'
      AND sc.name = 'التخطيط والتصميم'
    LIMIT 1
)
INSERT INTO sub_steps (building_step_id, title, description, sub_step_order, tips, warnings)
SELECT step.building_step_id, v.title, v.description, v.sub_step_order, v.tips, v.warnings
FROM step
CROSS JOIN (VALUES
('تحديد الاحتياجات', 'حدد عدد الغرف والمساحات المطلوبة وأسلوب الحياة', 1, 'فكر في المستقبل: هل ستحتاج غرف إضافية؟', NULL),
('اختيار المهندس', 'ابحث عن مهندس معماري ذو خبرة وسمعة جيدة', 2, 'اطلب رؤية أعمال سابقة للمهندس', 'تجنب المهندسين غير المرخصين'),
('مراجعة المخططات', 'راجع المخططات بدقة قبل الاعتماد النهائي', 3, 'تأكد من توزيع الغرف والإضاءة الطبيعية', NULL),
('اعتماد التصميم', 'الموافقة النهائية على التصميم والتوقيع', 4, 'احتفظ بنسخة من جميع المخططات', NULL)
) AS v(title, description, sub_step_order, tips, warnings)
WHERE NOT EXISTS (
    SELECT 1
    FROM sub_steps ss
    WHERE ss.building_step_id = step.building_step_id
      AND ss.title = v.title
      AND ss.sub_step_order = v.sub_step_order
);

-- خطوات فرعية لـ "صب السقف"
WITH step AS (
    SELECT bs.id AS building_step_id
    FROM building_steps bs
    JOIN step_categories sc ON sc.id = bs.category_id
    WHERE bs.title = 'صب السقف'
      AND sc.name = 'الهيكل الإنشائي'
    LIMIT 1
)
INSERT INTO sub_steps (building_step_id, title, description, sub_step_order, tips, warnings)
SELECT step.building_step_id, v.title, v.description, v.sub_step_order, v.tips, v.warnings
FROM step
CROSS JOIN (VALUES
('فحص التسليح', 'التأكد من صحة تسليح السقف قبل الصب', 1, 'استعن بمهندس للفحص', 'لا تصب قبل اعتماد المهندس'),
('تجهيز الخرسانة', 'طلب الخرسانة الجاهزة بالمواصفات المطلوبة', 2, 'تأكد من نسبة الخلط ونوع الإسمنت', 'لا تضف ماء زائد للخرسانة'),
('عملية الصب', 'صب الخرسانة بشكل متواصل مع الدمك', 3, 'استخدم هزاز كهربائي للدمك', 'تجنب الصب في الحر الشديد'),
('المعالجة', 'رش السقف بالماء لمدة 7 أيام', 4, 'غطِ السقف بالخيش المبلل', 'عدم المعالجة يضعف الخرسانة')
) AS v(title, description, sub_step_order, tips, warnings)
WHERE NOT EXISTS (
    SELECT 1
    FROM sub_steps ss
    WHERE ss.building_step_id = step.building_step_id
      AND ss.title = v.title
      AND ss.sub_step_order = v.sub_step_order
);

-- خطوات فرعية لـ "التمديدات الكهربائية"
WITH step AS (
    SELECT bs.id AS building_step_id
    FROM building_steps bs
    JOIN step_categories sc ON sc.id = bs.category_id
    WHERE bs.title = 'التمديدات الكهربائية'
      AND sc.name = 'التمديدات'
    LIMIT 1
)
INSERT INTO sub_steps (building_step_id, title, description, sub_step_order, tips, warnings)
SELECT step.building_step_id, v.title, v.description, v.sub_step_order, v.tips, v.warnings
FROM step
CROSS JOIN (VALUES
('تحديد نقاط الكهرباء', 'تحديد مواقع المفاتيح والأفياش والإضاءة', 1, 'ارسم مخطط كهربائي تفصيلي', NULL),
('تمديد المواسير', 'تمديد المواسير البلاستيكية داخل الجدران', 2, 'استخدم مواسير عالية الجودة', 'تجنب الانحناءات الحادة'),
('سحب الأسلاك', 'سحب الأسلاك الكهربائية داخل المواسير', 3, 'استخدم أسلاك نحاسية معتمدة', 'لا توصل الأسلاك داخل الجدار'),
('تركيب اللوحة', 'تركيب لوحة التوزيع الكهربائية', 4, 'اختر لوحة بسعة كافية للمستقبل', 'يجب أن يكون العمل بواسطة كهربائي مرخص')
) AS v(title, description, sub_step_order, tips, warnings)
WHERE NOT EXISTS (
    SELECT 1
    FROM sub_steps ss
    WHERE ss.building_step_id = step.building_step_id
      AND ss.title = v.title
      AND ss.sub_step_order = v.sub_step_order
);
