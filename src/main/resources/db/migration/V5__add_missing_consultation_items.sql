-- V5: Add missing consultation items for electrical, insulation, and roof types

-- =====================================================
-- Insert Electrical Types (أنواع الكهربائيات)
-- =====================================================

INSERT INTO consultation_items (category_id, code, name_ar, name_en, description_ar, price_from, price_to, price_unit, advantages, disadvantages, suitable_uses, specifications, tips) VALUES

((SELECT id FROM consultation_categories WHERE code = 'electrical_types'),
'wire_copper', 'أسلاك نحاسية', 'Copper Wires',
'الأسلاك النحاسية هي الأكثر استخداماً في التمديدات الكهربائية المنزلية.',
5000, 15000, 'لكل متر (حسب المقطع)',
'["موصلية عالية", "مرونة جيدة", "عمر طويل", "آمنة"]',
'["سعرها مرتفع نسبياً"]',
'["التمديدات المنزلية", "جميع أنواع البناء"]',
'{"cross_sections": "1.5, 2.5, 4, 6, 10, 16, 25 mm²", "insulation": "PVC", "voltage_rating": "450/750V"}',
'["استخدم مقاطع مناسبة للحمل", "1.5 للإنارة، 2.5 للبرايز، 4+ للمكيفات"]'),

((SELECT id FROM consultation_categories WHERE code = 'electrical_types'),
'wire_flexible', 'أسلاك مرنة (ليتز)', 'Flexible Wires',
'أسلاك مرنة متعددة الشعيرات للتوصيلات.',
3000, 10000, 'لكل متر (حسب المقطع)',
'["مرونة عالية جداً", "سهلة التركيب"]',
'["أقل تحملاً للحرارة"]',
'["توصيل الأجهزة", "اللوحات الكهربائية"]',
'{"cross_sections": "0.5, 0.75, 1, 1.5, 2.5 mm²"}',
'["لا تستخدم للتمديدات الثابتة داخل الجدران"]'),

((SELECT id FROM consultation_categories WHERE code = 'electrical_types'),
'switch_normal', 'مفاتيح عادية', 'Normal Switches',
'مفاتيح كهربائية عادية للإنارة.',
2000, 8000, 'لكل قطعة',
'["سعر اقتصادي", "سهلة التركيب", "متوفرة بكثرة"]',
'["تصميم بسيط"]',
'["الإنارة المنزلية", "جميع أنواع البناء"]',
'{"current_rating": "10-16A", "voltage": "250V"}',
'["اختر ماركات معروفة", "تأكد من جودة التوصيل الداخلي"]'),

((SELECT id FROM consultation_categories WHERE code = 'electrical_types'),
'switch_smart', 'مفاتيح ذكية', 'Smart Switches',
'مفاتيح ذكية يمكن التحكم بها عن بعد.',
25000, 80000, 'لكل قطعة',
'["تحكم عن بعد", "توفير الطاقة", "جدولة زمنية", "تصميم عصري"]',
'["سعر مرتفع", "تحتاج إنترنت"]',
'["البيوت الذكية", "الفلل الفاخرة"]',
'{"connectivity": "WiFi, Bluetooth", "compatibility": "Alexa, Google Home"}',
'["تأكد من توافقها مع نظام البيت الذكي"]'),

((SELECT id FROM consultation_categories WHERE code = 'electrical_types'),
'socket_normal', 'برايز عادية', 'Normal Sockets',
'مقابس كهربائية عادية.',
2000, 6000, 'لكل قطعة',
'["سعر اقتصادي", "متوفرة"]',
'["تصميم بسيط"]',
'["الاستخدام العام"]',
'{"current_rating": "13-16A", "voltage": "250V"}',
'["استخدم برايز بمفتاح أمان للأطفال"]'),

((SELECT id FROM consultation_categories WHERE code = 'electrical_types'),
'socket_usb', 'برايز مع USB', 'USB Sockets',
'مقابس كهربائية مع منافذ USB للشحن.',
8000, 20000, 'لكل قطعة',
'["شحن مباشر للأجهزة", "عملية", "توفر مساحة"]',
'["سعر أعلى"]',
'["غرف النوم", "المكاتب", "الصالات"]',
'{"usb_ports": "2-4", "usb_output": "5V 2.1A"}',
'["مثالية بجانب الأسرّة والمكاتب"]'),

((SELECT id FROM consultation_categories WHERE code = 'electrical_types'),
'breaker_mcb', 'قواطع MCB', 'MCB Circuit Breakers',
'قواطع دارة مصغرة للحماية من التحميل الزائد.',
5000, 15000, 'لكل قطعة',
'["حماية من التحميل الزائد", "إعادة تشغيل سهلة", "آمنة"]',
'[]',
'["جميع الدوائر الكهربائية"]',
'{"current_ratings": "6, 10, 16, 20, 25, 32, 40, 63A", "breaking_capacity": "6-10kA"}',
'["اختر أمبير مناسب للحمل", "استخدم ماركات معروفة"]'),

((SELECT id FROM consultation_categories WHERE code = 'electrical_types'),
'breaker_rccb', 'قواطع تسرب RCCB', 'RCCB Earth Leakage',
'قواطع للحماية من تسرب التيار الكهربائي.',
25000, 60000, 'لكل قطعة',
'["حماية من الصعق", "إنقاذ الأرواح", "ضرورية"]',
'["سعر مرتفع"]',
'["اللوحة الرئيسية", "الحمامات", "المطابخ"]',
'{"sensitivity": "30mA, 100mA, 300mA", "current_ratings": "25, 40, 63A"}',
'["ضرورية جداً للسلامة", "30mA للحماية الشخصية"]'),

((SELECT id FROM consultation_categories WHERE code = 'electrical_types'),
'panel_distribution', 'لوحة توزيع', 'Distribution Panel',
'لوحة توزيع كهربائية للمنزل.',
50000, 200000, 'لكل لوحة',
'["تنظيم الدوائر", "سهولة الصيانة", "حماية"]',
'[]',
'["كل منزل أو طابق"]',
'{"ways": "8, 12, 18, 24, 36 طريق"}',
'["اختر حجم أكبر من الحاجة الحالية", "اترك مساحة للتوسع"]')
ON CONFLICT (category_id, code) DO NOTHING;

-- =====================================================
-- Insert Insulation Types (أنواع العزل)
-- =====================================================

INSERT INTO consultation_items (category_id, code, name_ar, name_en, description_ar, price_from, price_to, price_unit, advantages, disadvantages, suitable_uses, specifications, tips) VALUES

((SELECT id FROM consultation_categories WHERE code = 'insulation_types'),
'insulation_bitumen', 'عزل بيتومين (القير)', 'Bitumen Insulation',
'العزل بالبيتومين أو القير هو الأكثر شيوعاً في العراق للعزل المائي.',
8000, 15000, 'لكل متر مربع',
'["سعر اقتصادي", "فعال للعزل المائي", "متوفر", "سهل التطبيق"]',
'["يتأثر بالحرارة العالية", "رائحة قوية", "يحتاج صيانة"]',
'["أسطح المباني", "الأساسات", "الحمامات"]',
'{"types": "بارد، حار", "thickness": "3-5 مم", "layers": "2-3 طبقات"}',
'["طبّق على سطح نظيف وجاف", "استخدم طبقتين على الأقل", "أضف طبقة حماية فوقه"]'),

((SELECT id FROM consultation_categories WHERE code = 'insulation_types'),
'insulation_membrane', 'رولات عزل (ممبرين)', 'Waterproof Membrane',
'رولات عزل مائي جاهزة تُلصق بالحرارة.',
15000, 35000, 'لكل متر مربع',
'["عزل ممتاز", "عمر طويل", "سهل التطبيق", "مقاوم للأشعة"]',
'["سعر أعلى", "يحتاج معدات لحام"]',
'["الأسطح", "الخزانات", "حمامات السباحة"]',
'{"thickness": "3-5 مم", "width": "1 متر", "length": "10 متر/رول"}',
'["تأكد من لحام الوصلات جيداً", "اختر نوع مقاوم للأشعة للأسطح المكشوفة"]'),

((SELECT id FROM consultation_categories WHERE code = 'insulation_types'),
'insulation_polyurethane', 'عزل بولي يوريثان (رش)', 'Polyurethane Spray',
'عزل رش بولي يوريثان للعزل الحراري والمائي معاً.',
25000, 50000, 'لكل متر مربع',
'["عزل حراري ومائي معاً", "يغطي الشقوق", "خفيف الوزن", "عمر طويل"]',
'["سعر مرتفع", "يحتاج معدات خاصة", "يتأثر بالأشعة"]',
'["الأسطح", "الجدران", "الخزانات"]',
'{"density": "35-45 كغ/م³", "thickness": "3-5 سم", "R-value": "6-7 لكل بوصة"}',
'["أفضل عزل حراري متاح", "يحتاج طبقة حماية من الشمس"]'),

((SELECT id FROM consultation_categories WHERE code = 'insulation_types'),
'insulation_eps', 'فلين (ستايروفوم) EPS', 'EPS Styrofoam',
'ألواح فلين للعزل الحراري.',
3000, 8000, 'لكل متر مربع',
'["سعر اقتصادي", "خفيف جداً", "سهل التركيب", "عزل حراري جيد"]',
'["قابل للاشتعال", "يتأثر بالمذيبات", "لا يعزل الماء"]',
'["الجدران", "الأسقف", "تحت البلاط"]',
'{"density": "15-25 كغ/م³", "thickness": "2-10 سم"}',
'["استخدمه مع عزل مائي", "غطّه بطبقة حماية"]'),

((SELECT id FROM consultation_categories WHERE code = 'insulation_types'),
'insulation_xps', 'فلين مضغوط XPS', 'XPS Extruded Polystyrene',
'فلين مضغوط عالي الكثافة للعزل الحراري.',
8000, 18000, 'لكل متر مربع',
'["مقاوم للماء", "قوة ضغط عالية", "عزل حراري ممتاز"]',
'["سعر أعلى من EPS"]',
'["تحت الأساسات", "الأسطح المعرضة للماء", "غرف التبريد"]',
'{"density": "30-45 كغ/م³", "thickness": "2-10 سم", "compressive_strength": "200-700 kPa"}',
'["مثالي للأماكن الرطبة", "يتحمل الأحمال"]'),

((SELECT id FROM consultation_categories WHERE code = 'insulation_types'),
'insulation_rockwool', 'صوف صخري', 'Rockwool',
'عزل من الصوف الصخري للعزل الحراري والصوتي.',
10000, 25000, 'لكل متر مربع',
'["عزل حراري وصوتي", "مقاوم للحريق", "صديق للبيئة"]',
'["يحتاج حماية من الماء", "قد يسبب حكة"]',
'["الجدران", "الأسقف المعلقة", "الفواصل"]',
'{"density": "40-150 كغ/م³", "thickness": "5-10 سم"}',
'["ممتاز للعزل الصوتي", "استخدم قفازات عند التركيب"]'),

((SELECT id FROM consultation_categories WHERE code = 'insulation_types'),
'insulation_ceramic', 'عزل سيراميكي (طلاء)', 'Ceramic Coating',
'طلاء عازل حراري يحتوي على جزيئات سيراميكية.',
15000, 30000, 'لكل متر مربع',
'["سهل التطبيق", "لا يحتاج سماكة", "يعكس الحرارة"]',
'["فعالية أقل من العوازل التقليدية"]',
'["الأسطح", "الجدران الخارجية", "خزانات المياه"]',
'{"thickness": "0.5-1 مم", "reflectivity": "80-90%"}',
'["مناسب للأسطح التي لا تتحمل أحمال إضافية"]')
ON CONFLICT (category_id, code) DO NOTHING;

-- =====================================================
-- Insert Roof Types (أنواع السقف) - إضافة للفئة الموجودة
-- =====================================================

-- تحديث اسم الفئة لتكون roof_types بدلاً من slab_types فقط
UPDATE consultation_categories
SET code = 'roof_types'
WHERE code = 'slab_types'
  AND NOT EXISTS (SELECT 1 FROM consultation_categories WHERE code = 'roof_types');

-- أو إضافة فئة جديدة إذا لم تكن موجودة
INSERT INTO consultation_categories (code, name_ar, name_en, description_ar, display_order)
SELECT 'roof_types', 'أنواع السقف', 'Roof Types', 'أنواع الأسقف والسقوف المختلفة', 4
WHERE NOT EXISTS (SELECT 1 FROM consultation_categories WHERE code = 'roof_types');
