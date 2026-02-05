-- إصلاح بيانات العقد التجريبية: تصحيح حالة العقد إلى القيمة الصحيحة في الـ ENUM

DO $$
BEGIN
    -- تحديث الحالات الخاطئة التي أدخلت بالقيمة PENDING_SIGNATURES إلى القيمة الصحيحة PENDING_SIGNATURE
    UPDATE contracts
    SET status = 'PENDING_SIGNATURE'
    WHERE status = 'PENDING_SIGNATURES';
END $$;
