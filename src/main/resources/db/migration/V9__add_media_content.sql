-- إضافة الصور والفيديوهات التوضيحية للخطوات

-- =====================================================
-- صور ورسومات مرحلة التخطيط والتصميم
-- =====================================================

-- شراء الأرض
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(1, 'IMAGE', 'https://images.unsplash.com/photo-1500382017468-9049fed747ef?w=800', 'قطعة أرض للبناء', 'اختر موقعاً مناسباً قريباً من الخدمات', 1),
(1, 'DIAGRAM', 'https://images.unsplash.com/photo-1589829545856-d10d557cf95f?w=800', 'خريطة الموقع', 'تحقق من موقع الأرض على الخريطة', 2),
(1, 'IMAGE', 'https://images.unsplash.com/photo-1450101499163-c8848c66ca85?w=800', 'فحص الأوراق', 'راجع جميع المستندات القانونية', 3);

-- التصميم المعماري
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(2, 'IMAGE', 'https://images.unsplash.com/photo-1503387762-592deb58ef4e?w=800', 'مخططات معمارية', 'المخططات الهندسية للمنزل', 1),
(2, 'DIAGRAM', 'https://images.unsplash.com/photo-1503387837-b154d5074bd2?w=800', 'المساقط الأفقية', 'توزيع الغرف والمساحات', 2),
(2, 'IMAGE', 'https://images.unsplash.com/photo-1581092160562-40aa08e78837?w=800', 'الواجهات المعمارية', 'تصميم واجهة المنزل', 3),
(2, 'VIDEO', 'https://www.youtube.com/watch?v=example1', 'شرح قراءة المخططات', 'كيفية فهم المخططات المعمارية', 4);

-- رخصة البناء
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(3, 'IMAGE', 'https://images.unsplash.com/photo-1450101499163-c8848c66ca85?w=800', 'رخصة البناء', 'نموذج رخصة البناء المعتمدة', 1),
(3, 'DIAGRAM', 'https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?w=800', 'خطوات استخراج الرخصة', 'الإجراءات المطلوبة', 2);

-- اختيار المقاول
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(4, 'IMAGE', 'https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=800', 'لقاء مع المقاول', 'مناقشة تفاصيل المشروع', 1),
(4, 'IMAGE', 'https://images.unsplash.com/photo-1450101499163-c8848c66ca85?w=800', 'عقد المقاولة', 'توثيق الاتفاق كتابياً', 2);

-- =====================================================
-- صور ورسومات مرحلة الأساسات
-- =====================================================

-- تنظيف وتسوية الأرض
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(5, 'IMAGE', 'https://images.unsplash.com/photo-1581092160562-40aa08e78837?w=800', 'تنظيف الموقع', 'إزالة العوائق والمخلفات', 1),
(5, 'IMAGE', 'https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=800', 'تسوية الأرض', 'استخدام المعدات الثقيلة', 2),
(5, 'VIDEO', 'https://www.youtube.com/watch?v=example2', 'عملية التسوية', 'خطوات تسوية الأرض', 3);

-- حفر الأساسات
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(6, 'IMAGE', 'https://images.unsplash.com/photo-1541888946425-d81bb19240f5?w=800', 'حفر القواعد', 'حفر خنادق الأساسات', 1),
(6, 'DIAGRAM', 'https://images.unsplash.com/photo-1503387762-592deb58ef4e?w=800', 'مخطط الحفر', 'أبعاد وأعماق الحفر', 2),
(6, 'IMAGE', 'https://images.unsplash.com/photo-1581092160562-40aa08e78837?w=800', 'الحفر بالمعدات', 'استخدام الحفارة', 3);

-- صب الخرسانة العادية
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(7, 'IMAGE', 'https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=800', 'طبقة النظافة', 'صب الخرسانة العادية', 1),
(7, 'DIAGRAM', 'https://images.unsplash.com/photo-1503387762-592deb58ef4e?w=800', 'سماكة طبقة النظافة', 'رسم توضيحي للطبقة', 2);

-- تسليح الأساسات
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(8, 'IMAGE', 'https://images.unsplash.com/photo-1581092160562-40aa08e78837?w=800', 'حديد التسليح', 'تركيب حديد القواعد', 1),
(8, 'DIAGRAM', 'https://images.unsplash.com/photo-1503387762-592deb58ef4e?w=800', 'مخطط التسليح', 'توزيع الحديد في القواعد', 2),
(8, 'IMAGE', 'https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=800', 'ربط الحديد', 'ربط أسياخ الحديد', 3),
(8, 'VIDEO', 'https://www.youtube.com/watch?v=example3', 'طريقة تسليح القواعد', 'شرح عملي للتسليح', 4);

-- صب القواعد
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(9, 'IMAGE', 'https://images.unsplash.com/photo-1541888946425-d81bb19240f5?w=800', 'صب الخرسانة', 'صب خرسانة القواعد', 1),
(9, 'IMAGE', 'https://images.unsplash.com/photo-1581092160562-40aa08e78837?w=800', 'الدمك بالهزاز', 'دمك الخرسانة', 2),
(9, 'VIDEO', 'https://www.youtube.com/watch?v=example4', 'عملية الصب الكاملة', 'من البداية للنهاية', 3);

-- بناء الرقاب
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(10, 'IMAGE', 'https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=800', 'رقاب الأعمدة', 'بناء الرقاب بالطابوق', 1),
(10, 'DIAGRAM', 'https://images.unsplash.com/photo-1503387762-592deb58ef4e?w=800', 'مقطع الرقبة', 'رسم توضيحي للرقبة', 2);

-- الردم والدك
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(11, 'IMAGE', 'https://images.unsplash.com/photo-1581092160562-40aa08e78837?w=800', 'ردم التربة', 'ردم حول الأساسات', 1),
(11, 'IMAGE', 'https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=800', 'الدك الميكانيكي', 'دك التربة بالمعدات', 2);

-- =====================================================
-- صور ورسومات مرحلة الهيكل الإنشائي
-- =====================================================

-- صب الميدة
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(12, 'IMAGE', 'https://images.unsplash.com/photo-1541888946425-d81bb19240f5?w=800', 'الميدة الأرضية', 'الجسور الأرضية', 1),
(12, 'DIAGRAM', 'https://images.unsplash.com/photo-1503387762-592deb58ef4e?w=800', 'مخطط الميدة', 'توزيع الميدة', 2);

-- بناء الأعمدة
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(13, 'IMAGE', 'https://images.unsplash.com/photo-1581092160562-40aa08e78837?w=800', 'تسليح الأعمدة', 'حديد تسليح العمود', 1),
(13, 'IMAGE', 'https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=800', 'شدة العمود', 'القوالب الخشبية', 2),
(13, 'IMAGE', 'https://images.unsplash.com/photo-1541888946425-d81bb19240f5?w=800', 'صب العمود', 'صب خرسانة العمود', 3),
(13, 'VIDEO', 'https://www.youtube.com/watch?v=example5', 'خطوات بناء العمود', 'شرح كامل', 4);

-- تركيب الشدة الخشبية
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(14, 'IMAGE', 'https://images.unsplash.com/photo-1581092160562-40aa08e78837?w=800', 'الشدة الخشبية', 'تركيب القوالب للسقف', 1),
(14, 'IMAGE', 'https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=800', 'دعامات الشدة', 'الدعامات الحديدية', 2),
(14, 'DIAGRAM', 'https://images.unsplash.com/photo-1503387762-592deb58ef4e?w=800', 'مقطع الشدة', 'رسم توضيحي', 3);

-- تسليح السقف
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(15, 'IMAGE', 'https://images.unsplash.com/photo-1541888946425-d81bb19240f5?w=800', 'حديد السقف', 'تسليح البلاطة', 1),
(15, 'IMAGE', 'https://images.unsplash.com/photo-1581092160562-40aa08e78837?w=800', 'حديد الجسور', 'تسليح الكمرات', 2),
(15, 'DIAGRAM', 'https://images.unsplash.com/photo-1503387762-592deb58ef4e?w=800', 'مخطط التسليح', 'توزيع الحديد', 3),
(15, 'VIDEO', 'https://www.youtube.com/watch?v=example6', 'تسليح السقف خطوة بخطوة', 'شرح تفصيلي', 4);

-- صب السقف
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(16, 'IMAGE', 'https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=800', 'صب السقف', 'عملية صب الخرسانة', 1),
(16, 'IMAGE', 'https://images.unsplash.com/photo-1541888946425-d81bb19240f5?w=800', 'الخرسانة الجاهزة', 'شاحنة الخرسانة', 2),
(16, 'IMAGE', 'https://images.unsplash.com/photo-1581092160562-40aa08e78837?w=800', 'المعالجة بالماء', 'رش السقف بالماء', 3),
(16, 'VIDEO', 'https://www.youtube.com/watch?v=example7', 'عملية صب السقف الكاملة', 'من البداية للنهاية', 4);

-- فك الشدة
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(17, 'IMAGE', 'https://images.unsplash.com/photo-1581092160562-40aa08e78837?w=800', 'فك الشدة', 'إزالة القوالب', 1),
(17, 'IMAGE', 'https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=800', 'السقف بعد الفك', 'الشكل النهائي', 2);

-- =====================================================
-- صور ورسومات مرحلة البناء والجدران
-- =====================================================

-- بناء الجدران الخارجية
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(18, 'IMAGE', 'https://images.unsplash.com/photo-1541888946425-d81bb19240f5?w=800', 'بناء الجدران', 'بناء بالطابوق الأحمر', 1),
(18, 'IMAGE', 'https://images.unsplash.com/photo-1581092160562-40aa08e78837?w=800', 'المونة الإسمنتية', 'تحضير المونة', 2),
(18, 'DIAGRAM', 'https://images.unsplash.com/photo-1503387762-592deb58ef4e?w=800', 'طريقة البناء', 'رسم توضيحي للبناء', 3),
(18, 'VIDEO', 'https://www.youtube.com/watch?v=example8', 'تقنيات البناء الصحيحة', 'شرح عملي', 4);

-- بناء الجدران الداخلية
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(19, 'IMAGE', 'https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=800', 'الجدران الداخلية', 'فواصل الغرف', 1),
(19, 'IMAGE', 'https://images.unsplash.com/photo-1541888946425-d81bb19240f5?w=800', 'فتحات الأبواب', 'ترك الفتحات', 2);

-- تركيب الحلوق
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(20, 'IMAGE', 'https://images.unsplash.com/photo-1581092160562-40aa08e78837?w=800', 'حلوق الأبواب', 'تركيب الإطارات', 1),
(20, 'IMAGE', 'https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=800', 'حلوق النوافذ', 'إطارات النوافذ', 2);

-- العزل الحراري
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(21, 'IMAGE', 'https://images.unsplash.com/photo-1541888946425-d81bb19240f5?w=800', 'ألواح العزل', 'مواد العزل الحراري', 1),
(21, 'IMAGE', 'https://images.unsplash.com/photo-1581092160562-40aa08e78837?w=800', 'تركيب العزل', 'تثبيت العازل', 2),
(21, 'DIAGRAM', 'https://images.unsplash.com/photo-1503387762-592deb58ef4e?w=800', 'طبقات العزل', 'رسم توضيحي', 3);

-- =====================================================
-- صور ورسومات مرحلة التمديدات
-- =====================================================

-- تمديدات الصرف الصحي
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(22, 'IMAGE', 'https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=800', 'أنابيب الصرف', 'تمديد أنابيب PVC', 1),
(22, 'DIAGRAM', 'https://images.unsplash.com/photo-1503387762-592deb58ef4e?w=800', 'مخطط الصرف', 'توزيع الأنابيب', 2),
(22, 'VIDEO', 'https://www.youtube.com/watch?v=example9', 'تمديد الصرف الصحي', 'شرح كامل', 3);

-- تمديدات المياه
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(23, 'IMAGE', 'https://images.unsplash.com/photo-1541888946425-d81bb19240f5?w=800', 'أنابيب المياه', 'تمديد أنابيب PPR', 1),
(23, 'IMAGE', 'https://images.unsplash.com/photo-1581092160562-40aa08e78837?w=800', 'لحام الأنابيب', 'لحام أنابيب البلاستيك', 2),
(23, 'DIAGRAM', 'https://images.unsplash.com/photo-1503387762-592deb58ef4e?w=800', 'مخطط المياه', 'توزيع نقاط المياه', 3);

-- التمديدات الكهربائية
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(24, 'IMAGE', 'https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=800', 'المواسير الكهربائية', 'تمديد المواسير', 1),
(24, 'IMAGE', 'https://images.unsplash.com/photo-1541888946425-d81bb19240f5?w=800', 'سحب الأسلاك', 'الأسلاك الكهربائية', 2),
(24, 'DIAGRAM', 'https://images.unsplash.com/photo-1503387762-592deb58ef4e?w=800', 'المخطط الكهربائي', 'توزيع النقاط', 3),
(24, 'VIDEO', 'https://www.youtube.com/watch?v=example10', 'التمديدات الكهربائية المنزلية', 'دليل شامل', 4);

-- تمديدات التكييف
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(25, 'IMAGE', 'https://images.unsplash.com/photo-1581092160562-40aa08e78837?w=800', 'مواسير التكييف', 'المواسير النحاسية', 1),
(25, 'IMAGE', 'https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=800', 'مجاري التكييف', 'مجاري الصرف', 2);

-- تمديدات الغاز
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(26, 'IMAGE', 'https://images.unsplash.com/photo-1541888946425-d81bb19240f5?w=800', 'أنابيب الغاز', 'التمديدات الغازية', 1),
(26, 'DIAGRAM', 'https://images.unsplash.com/photo-1503387762-592deb58ef4e?w=800', 'مخطط الغاز', 'توزيع نقاط الغاز', 2);

-- =====================================================
-- صور ورسومات مرحلة التشطيبات
-- =====================================================

-- اللياسة الداخلية
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(27, 'IMAGE', 'https://images.unsplash.com/photo-1581092160562-40aa08e78837?w=800', 'لياسة الجدران', 'تلبيس الجدران', 1),
(27, 'IMAGE', 'https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=800', 'التسوية بالقدة', 'استخدام القدة', 2),
(27, 'VIDEO', 'https://www.youtube.com/watch?v=example11', 'طريقة اللياسة الصحيحة', 'شرح عملي', 3);

-- اللياسة الخارجية
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(28, 'IMAGE', 'https://images.unsplash.com/photo-1541888946425-d81bb19240f5?w=800', 'لياسة الواجهات', 'تلبيس الواجهة', 1),
(28, 'IMAGE', 'https://images.unsplash.com/photo-1581092160562-40aa08e78837?w=800', 'اللياسة الحجرية', 'تشطيب بالحجر', 2);

-- العزل المائي
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(29, 'IMAGE', 'https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=800', 'عزل الحمامات', 'مواد العزل المائي', 1),
(29, 'IMAGE', 'https://images.unsplash.com/photo-1541888946425-d81bb19240f5?w=800', 'عزل الأسطح', 'عزل السطح', 2),
(29, 'VIDEO', 'https://www.youtube.com/watch?v=example12', 'طريقة العزل المائي', 'خطوات العزل', 3);

-- تركيب السيراميك
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(30, 'IMAGE', 'https://images.unsplash.com/photo-1581092160562-40aa08e78837?w=800', 'تركيب البلاط', 'لصق السيراميك', 1),
(30, 'IMAGE', 'https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=800', 'تسوية البلاط', 'استخدام الميزان', 2),
(30, 'IMAGE', 'https://images.unsplash.com/photo-1541888946425-d81bb19240f5?w=800', 'ملء الفواصل', 'مونة الفواصل', 3),
(30, 'VIDEO', 'https://www.youtube.com/watch?v=example13', 'تركيب السيراميك احترافياً', 'دليل كامل', 4);

-- الدهانات
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(31, 'IMAGE', 'https://images.unsplash.com/photo-1581092160562-40aa08e78837?w=800', 'دهان الجدران', 'طلاء الجدران', 1),
(31, 'IMAGE', 'https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=800', 'أدوات الدهان', 'الرولر والفرش', 2),
(31, 'IMAGE', 'https://images.unsplash.com/photo-1541888946425-d81bb19240f5?w=800', 'ألوان الدهان', 'اختيار الألوان', 3),
(31, 'VIDEO', 'https://www.youtube.com/watch?v=example14', 'تقنيات الدهان الحديثة', 'نصائح احترافية', 4);

-- تركيب الجبس
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(32, 'IMAGE', 'https://images.unsplash.com/photo-1581092160562-40aa08e78837?w=800', 'أسقف جبس بورد', 'تركيب الجبس', 1),
(32, 'IMAGE', 'https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=800', 'ديكورات الجبس', 'أشكال جبسية', 2),
(32, 'VIDEO', 'https://www.youtube.com/watch?v=example15', 'تركيب الجبس بورد', 'شرح تفصيلي', 3);

-- =====================================================
-- صور ورسومات مرحلة التشطيبات النهائية
-- =====================================================

-- تركيب الأبواب
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(33, 'IMAGE', 'https://images.unsplash.com/photo-1541888946425-d81bb19240f5?w=800', 'تركيب الأبواب', 'أبواب خشبية', 1),
(33, 'IMAGE', 'https://images.unsplash.com/photo-1581092160562-40aa08e78837?w=800', 'الباب الخارجي', 'الباب الرئيسي', 2);

-- تركيب النوافذ
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(34, 'IMAGE', 'https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=800', 'نوافذ ألمنيوم', 'تركيب النوافذ', 1),
(34, 'IMAGE', 'https://images.unsplash.com/photo-1541888946425-d81bb19240f5?w=800', 'الزجاج المزدوج', 'نوافذ عازلة', 2);

-- تركيب الأدوات الصحية
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(35, 'IMAGE', 'https://images.unsplash.com/photo-1581092160562-40aa08e78837?w=800', 'الأدوات الصحية', 'المراحيض والمغاسل', 1),
(35, 'IMAGE', 'https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=800', 'الخلاطات', 'خلاطات حديثة', 2),
(35, 'VIDEO', 'https://www.youtube.com/watch?v=example16', 'تركيب الأدوات الصحية', 'خطوات التركيب', 3);

-- تركيب الأجهزة الكهربائية
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(36, 'IMAGE', 'https://images.unsplash.com/photo-1541888946425-d81bb19240f5?w=800', 'المفاتيح والأفياش', 'تركيب النقاط', 1),
(36, 'IMAGE', 'https://images.unsplash.com/photo-1581092160562-40aa08e78837?w=800', 'الإضاءة', 'تركيب الإنارة', 2);

-- تركيب المطبخ
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(37, 'IMAGE', 'https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=800', 'خزائن المطبخ', 'تركيب الخزائن', 1),
(37, 'IMAGE', 'https://images.unsplash.com/photo-1541888946425-d81bb19240f5?w=800', 'رخام المطبخ', 'تركيب الرخام', 2),
(37, 'IMAGE', 'https://images.unsplash.com/photo-1581092160562-40aa08e78837?w=800', 'مطبخ حديث', 'المطبخ النهائي', 3),
(37, 'VIDEO', 'https://www.youtube.com/watch?v=example17', 'تصميم وتركيب المطبخ', 'دليل شامل', 4);

-- الأعمال الخارجية
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(38, 'IMAGE', 'https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=800', 'تنسيق الحديقة', 'زراعة الحديقة', 1),
(38, 'IMAGE', 'https://images.unsplash.com/photo-1541888946425-d81bb19240f5?w=800', 'السور الخارجي', 'بناء السور', 2),
(38, 'IMAGE', 'https://images.unsplash.com/photo-1581092160562-40aa08e78837?w=800', 'موقف السيارات', 'تعبيد الموقف', 3);

-- التنظيف والتسليم
INSERT INTO step_media (building_step_id, media_type, url, title, caption, media_order) VALUES
(39, 'IMAGE', 'https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=800', 'تنظيف المنزل', 'التنظيف النهائي', 1),
(39, 'IMAGE', 'https://images.unsplash.com/photo-1541888946425-d81bb19240f5?w=800', 'المنزل الجاهز', 'المنزل بعد الانتهاء', 2),
(39, 'IMAGE', 'https://images.unsplash.com/photo-1581092160562-40aa08e78837?w=800', 'تسليم المفاتيح', 'استلام المنزل', 3);
