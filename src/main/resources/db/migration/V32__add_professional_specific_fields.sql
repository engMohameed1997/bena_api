-- إضافة حقول خاصة بالمهندسين والمقاولين والمصممين

-- ========== حقول المهندسين ==========
-- هوية النقابة
ALTER TABLE workers ADD COLUMN IF NOT EXISTS syndicate_id VARCHAR(50);
ALTER TABLE workers ADD COLUMN IF NOT EXISTS syndicate_card_url VARCHAR(500);

-- الشهادة/الإجازة
ALTER TABLE workers ADD COLUMN IF NOT EXISTS degree VARCHAR(100);
ALTER TABLE workers ADD COLUMN IF NOT EXISTS degree_certificate_url VARCHAR(500);

-- التخصص
ALTER TABLE workers ADD COLUMN IF NOT EXISTS specialization VARCHAR(100);

-- عدد المشاريع المنجزة
ALTER TABLE workers ADD COLUMN IF NOT EXISTS completed_projects_count INTEGER DEFAULT 0;

-- ========== حقول المقاولين ==========
-- رخصة المقاولة
ALTER TABLE workers ADD COLUMN IF NOT EXISTS contractor_license VARCHAR(50);
ALTER TABLE workers ADD COLUMN IF NOT EXISTS contractor_license_url VARCHAR(500);

-- نوع الرخصة (A, B, C, D)
ALTER TABLE workers ADD COLUMN IF NOT EXISTS license_type VARCHAR(10);

-- تاريخ انتهاء الرخصة
ALTER TABLE workers ADD COLUMN IF NOT EXISTS license_expiry_date DATE;

-- رأس المال
ALTER TABLE workers ADD COLUMN IF NOT EXISTS capital DECIMAL(15,2);

-- عدد العمال
ALTER TABLE workers ADD COLUMN IF NOT EXISTS employees_count INTEGER;

-- عدد المشاريع الحالية
ALTER TABLE workers ADD COLUMN IF NOT EXISTS current_projects_count INTEGER DEFAULT 0;

-- ========== حقول المصممين ==========
-- نوع التصميم (interior, exterior, landscape, architectural)
ALTER TABLE workers ADD COLUMN IF NOT EXISTS design_type VARCHAR(50);

-- البرامج المستخدمة (AutoCAD, 3DMax, SketchUp, etc.)
ALTER TABLE workers ADD COLUMN IF NOT EXISTS software_skills TEXT;

-- رابط معرض الأعمال
ALTER TABLE workers ADD COLUMN IF NOT EXISTS portfolio_url VARCHAR(500);

-- الأسلوب (modern, classic, minimalist, industrial, etc.)
ALTER TABLE workers ADD COLUMN IF NOT EXISTS design_style VARCHAR(50);

-- ========== حقول عامة إضافية ==========
-- سنوات الخبرة المتخصصة
ALTER TABLE workers ADD COLUMN IF NOT EXISTS specialized_experience_years INTEGER;

-- الجوائز والشهادات الإضافية
ALTER TABLE workers ADD COLUMN IF NOT EXISTS awards TEXT;

-- اللغات
ALTER TABLE workers ADD COLUMN IF NOT EXISTS languages VARCHAR(255);

-- ساعات العمل
ALTER TABLE workers ADD COLUMN IF NOT EXISTS working_hours VARCHAR(100);

-- الحد الأدنى للمشروع
ALTER TABLE workers ADD COLUMN IF NOT EXISTS minimum_project_budget DECIMAL(15,2);

-- ========== فهارس للأداء ==========
CREATE INDEX IF NOT EXISTS idx_workers_syndicate_id ON workers(syndicate_id);
CREATE INDEX IF NOT EXISTS idx_workers_contractor_license ON workers(contractor_license);
CREATE INDEX IF NOT EXISTS idx_workers_specialization ON workers(specialization);
CREATE INDEX IF NOT EXISTS idx_workers_design_type ON workers(design_type);
CREATE INDEX IF NOT EXISTS idx_workers_license_expiry ON workers(license_expiry_date);

-- ========== تعليقات على الأعمدة ==========
COMMENT ON COLUMN workers.syndicate_id IS 'رقم هوية النقابة للمهندسين';
COMMENT ON COLUMN workers.degree IS 'الشهادة الأكاديمية (بكالوريوس، ماجستير، دكتوراه)';
COMMENT ON COLUMN workers.specialization IS 'التخصص الدقيق (معماري، مدني، كهرباء، ميكانيك)';
COMMENT ON COLUMN workers.contractor_license IS 'رقم رخصة المقاولة';
COMMENT ON COLUMN workers.license_type IS 'نوع الرخصة (A: مشاريع كبيرة، B: متوسطة، C: صغيرة، D: محدودة)';
COMMENT ON COLUMN workers.design_type IS 'نوع التصميم (interior: داخلي، exterior: خارجي، landscape: حدائق)';
COMMENT ON COLUMN workers.software_skills IS 'البرامج المستخدمة مفصولة بفواصل';
COMMENT ON COLUMN workers.design_style IS 'الأسلوب (modern، classic، minimalist، industrial)';
