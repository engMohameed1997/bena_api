-- ضمان وجود الأعمدة الأساسية في جدول workers لتتوافق مع كيان Worker الحالي

DO $$
BEGIN
    -- الاسم
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'workers' AND column_name = 'name'
    ) THEN
        ALTER TABLE workers ADD COLUMN name VARCHAR(255);
    END IF;

    -- محاولة تعبئة name من full_name إن وجد
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'workers' AND column_name = 'full_name'
    ) THEN
        EXECUTE 'UPDATE workers SET name = full_name WHERE name IS NULL';
    END IF;

    -- التصنيف
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'workers' AND column_name = 'category'
    ) THEN
        ALTER TABLE workers ADD COLUMN category VARCHAR(50);
    END IF;

    -- الوصف
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'workers' AND column_name = 'description'
    ) THEN
        ALTER TABLE workers ADD COLUMN description TEXT;
    END IF;

    -- الهاتف والواتساب
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'workers' AND column_name = 'phone_number'
    ) THEN
        ALTER TABLE workers ADD COLUMN phone_number VARCHAR(20);
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'workers' AND column_name = 'whatsapp_number'
    ) THEN
        ALTER TABLE workers ADD COLUMN whatsapp_number VARCHAR(20);
    END IF;

    -- الصور القديمة
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'workers' AND column_name = 'profile_image'
    ) THEN
        ALTER TABLE workers ADD COLUMN profile_image BYTEA;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'workers' AND column_name = 'profile_image_type'
    ) THEN
        ALTER TABLE workers ADD COLUMN profile_image_type VARCHAR(50);
    END IF;

    -- التقييمات
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'workers' AND column_name = 'average_rating'
    ) THEN
        ALTER TABLE workers ADD COLUMN average_rating DOUBLE PRECISION DEFAULT 0.0;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'workers' AND column_name = 'review_count'
    ) THEN
        ALTER TABLE workers ADD COLUMN review_count INTEGER DEFAULT 0;
    END IF;

    -- الحالة
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'workers' AND column_name = 'is_featured'
    ) THEN
        ALTER TABLE workers ADD COLUMN is_featured BOOLEAN DEFAULT FALSE;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'workers' AND column_name = 'is_active'
    ) THEN
        ALTER TABLE workers ADD COLUMN is_active BOOLEAN DEFAULT TRUE;
    END IF;

    -- التواريخ
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'workers' AND column_name = 'created_at'
    ) THEN
        ALTER TABLE workers ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'workers' AND column_name = 'updated_at'
    ) THEN
        ALTER TABLE workers ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
    END IF;
END $$;
