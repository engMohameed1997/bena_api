-- Insert sample job requests with UUID user_id
-- This adds test data after the UUID migration

-- Only insert if we have both users and workers
DO $$
DECLARE
    v_user_id UUID;
    v_worker_id_1 BIGINT;
    v_worker_id_2 BIGINT;
    v_worker_id_3 BIGINT;
BEGIN
    -- Get user ID
    SELECT id INTO v_user_id FROM users WHERE email = 'm@gmail.com' LIMIT 1;
    
    -- Get worker IDs
    SELECT id INTO v_worker_id_1 FROM workers ORDER BY id LIMIT 1;
    SELECT id INTO v_worker_id_2 FROM workers ORDER BY id LIMIT 1 OFFSET 1;
    SELECT id INTO v_worker_id_3 FROM workers ORDER BY id LIMIT 1 OFFSET 2;
    
    -- Only proceed if we have the required data
    IF v_user_id IS NOT NULL AND v_worker_id_1 IS NOT NULL THEN
        -- Request 1: Pending request
        INSERT INTO job_requests (
            user_id, worker_id, job_type, description, 
            location_city, location_area, latitude, longitude, 
            budget, status, created_at, updated_at
        ) VALUES (
            v_user_id, v_worker_id_1,
            'بناء منزل كامل',
            'أحتاج إلى بناء منزل من طابقين في منطقة الكرادة',
            'بغداد', 'الكرادة',
            33.3152, 44.3661,
            50000000, 'PENDING',
            NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days'
        );
    END IF;
    
    IF v_user_id IS NOT NULL AND v_worker_id_2 IS NOT NULL THEN
        -- Request 2: Offer sent
        INSERT INTO job_requests (
            user_id, worker_id, job_type, description, 
            location_city, location_area, latitude, longitude, 
            budget, status, worker_price_offer, worker_response,
            created_at, updated_at
        ) VALUES (
            v_user_id, v_worker_id_2,
            'تصميم خريطة منزل',
            'تصميم خريطة لمنزل مساحة 200 متر',
            'بغداد', 'الجادرية',
            33.2778, 44.3889,
            5000000, 'OFFER_SENT',
            4500000, 'يمكنني تصميم الخريطة خلال أسبوع بسعر مناسب',
            NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day'
        );
    END IF;
    
    IF v_user_id IS NOT NULL AND v_worker_id_3 IS NOT NULL THEN
        -- Request 3: Accepted
        INSERT INTO job_requests (
            user_id, worker_id, job_type, description, 
            location_city, location_area, latitude, longitude, 
            budget, status, created_at, updated_at
        ) VALUES (
            v_user_id, v_worker_id_3,
            'تمديدات كهربائية',
            'تمديدات كهربائية لمنزل جديد',
            'بغداد', 'المنصور',
            33.3128, 44.3615,
            3000000, 'ACCEPTED',
            NOW() - INTERVAL '3 days', NOW() - INTERVAL '1 hour'
        );
    END IF;
END $$;
