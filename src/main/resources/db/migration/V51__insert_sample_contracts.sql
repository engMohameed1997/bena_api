-- إدراج بيانات تجريبية للعقود والمشاريع

DO $$
DECLARE
    v_user1_id UUID;
    v_user2_id UUID;
    v_project1_id UUID;
    v_project2_id UUID;
BEGIN
    -- الحصول على معرفات المستخدمين الموجودين
    SELECT id INTO v_user1_id FROM users WHERE email = 'a@gmail.com' LIMIT 1;
    SELECT id INTO v_user2_id FROM users WHERE email = 'm@gmail.com' LIMIT 1;

    -- التحقق من وجود المستخدمين
    IF v_user1_id IS NULL OR v_user2_id IS NULL THEN
        RAISE NOTICE 'Users not found, skipping sample data insertion';
        RETURN;
    END IF;

    -- إنشاء مشروع تجريبي 1
    INSERT INTO projects (
        id,
        title,
        description,
        project_type,
        client_id,
        provider_id,
        total_budget,
        platform_commission_percentage,
        platform_commission_amount,
        provider_amount,
        location_city,
        location_area,
        status,
        start_date,
        expected_end_date,
        created_at,
        updated_at
    ) VALUES (
        gen_random_uuid(),
        'بناء منزل عائلي',
        'بناء منزل عائلي من طابقين بمساحة 300 متر مربع',
        'CONSTRUCTION',
        v_user1_id,
        v_user2_id,
        150000000.00,
        10.00,
        15000000.00,
        135000000.00,
        'بغداد',
        'الكرادة',
        'IN_PROGRESS',
        CURRENT_DATE,
        CURRENT_DATE + INTERVAL '6 months',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    )
    RETURNING id INTO v_project1_id;

    -- إنشاء عقد للمشروع 1
    INSERT INTO contracts (
        id,
        project_id,
        client_id,
        provider_id,
        contract_terms,
        payment_terms,
        delivery_terms,
        cancellation_policy,
        client_signed,
        client_signed_at,
        provider_signed,
        provider_signed_at,
        status,
        contract_start_date,
        contract_end_date,
        notes,
        created_at,
        updated_at
    ) VALUES (
        gen_random_uuid(),
        v_project1_id,
        v_user1_id,
        v_user2_id,
        'يلتزم المقاول بتنفيذ جميع الأعمال وفق المواصفات المتفق عليها والمخططات المعتمدة. مدة التنفيذ 6 أشهر من تاريخ التوقيع. ضمان سنة واحدة على جميع الأعمال.',
        'الدفع على 4 مراحل: 25% عند بدء الأساسات، 30% عند إنجاز الهيكل الإنشائي، 30% عند التشطيبات الداخلية، 15% عند التسليم النهائي. عمولة المنصة 10% من قيمة العقد.',
        'التسليم النهائي خلال 6 أشهر من تاريخ التوقيع. التسليم على مراحل حسب نسبة الإنجاز.',
        'في حالة الإلغاء من قبل العميل يتم خصم 10% من المبلغ المدفوع. في حالة الإلغاء من قبل المقاول يتم إرجاع كامل المبلغ مع تعويض 5%.',
        true,
        CURRENT_TIMESTAMP - INTERVAL '5 days',
        true,
        CURRENT_TIMESTAMP - INTERVAL '4 days',
        'ACTIVE',
        CURRENT_DATE,
        CURRENT_DATE + INTERVAL '6 months',
        'عقد موثق من خلال منصة بنا',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );

    -- إنشاء مشروع تجريبي 2
    INSERT INTO projects (
        id,
        title,
        description,
        project_type,
        client_id,
        provider_id,
        total_budget,
        platform_commission_percentage,
        platform_commission_amount,
        provider_amount,
        location_city,
        location_area,
        status,
        start_date,
        expected_end_date,
        created_at,
        updated_at
    ) VALUES (
        gen_random_uuid(),
        'تجديد شقة سكنية',
        'تجديد شقة سكنية شامل التشطيبات والديكورات',
        'RENOVATION',
        v_user2_id,
        v_user1_id,
        45000000.00,
        10.00,
        4500000.00,
        40500000.00,
        'بغداد',
        'المنصور',
        'PLANNING',
        CURRENT_DATE + INTERVAL '1 week',
        CURRENT_DATE + INTERVAL '3 months',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    )
    RETURNING id INTO v_project2_id;

    -- إنشاء عقد للمشروع 2 (في انتظار التوقيع)
    INSERT INTO contracts (
        id,
        project_id,
        client_id,
        provider_id,
        contract_terms,
        payment_terms,
        delivery_terms,
        cancellation_policy,
        client_signed,
        provider_signed,
        status,
        contract_start_date,
        contract_end_date,
        notes,
        created_at,
        updated_at
    ) VALUES (
        gen_random_uuid(),
        v_project2_id,
        v_user2_id,
        v_user1_id,
        'تجديد شامل للشقة يشمل الأرضيات والجدران والأسقف والكهرباء والسباكة. مدة التنفيذ 3 أشهر.',
        'الدفع على 3 مراحل: 40% عند البدء، 40% عند إنجاز 70%، 20% عند التسليم النهائي. عمولة المنصة 10%.',
        'التسليم خلال 3 أشهر من تاريخ البدء.',
        'في حالة الإلغاء يتم التفاوض على التعويضات حسب نسبة الإنجاز.',
        true,
        false,
        'PENDING_SIGNATURE',
        CURRENT_DATE + INTERVAL '1 week',
        CURRENT_DATE + INTERVAL '3 months',
        'في انتظار توقيع المقاول',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    );

    RAISE NOTICE 'Sample contracts and projects inserted successfully';
END $$;
