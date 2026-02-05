package com.bena.api.common.constant;

import java.util.*;

/**
 * قاعدة بيانات محافظات العراق ومدنها
 */
public class IraqGovernorates {

    public static final Map<String, List<String>> GOVERNORATES_CITIES = new LinkedHashMap<>();

    static {
        // بغداد
        GOVERNORATES_CITIES.put("بغداد", Arrays.asList(
                "الكرخ", "الرصافة", "الكاظمية", "الأعظمية", "المنصور", "الدورة", 
                "المحمودية", "أبو غريب", "الطارمية"
        ));

        // البصرة
        GOVERNORATES_CITIES.put("البصرة", Arrays.asList(
                "البصرة", "أبو الخصيب", "الزبير", "القرنة", "الفاو", 
                "الهارثة", "شط العرب", "المدينة"
        ));

        // نينوى
        GOVERNORATES_CITIES.put("نينوى", Arrays.asList(
                "الموصل", "تلعفر", "سنجار", "الحمدانية", "الشيخان"

,
                "تلكيف", "القيارة", "البعاج", "الحضر"
        ));

        // أربيل
        GOVERNORATES_CITIES.put("أربيل", Arrays.asList(
                "أربيل", "كويسنجق", "راوندوز", "شقلاوة", "سوران", "خبات", "مخمور"
        ));

        // السليمانية
        GOVERNORATES_CITIES.put("السليمانية", Arrays.asList(
                "السليمانية", "حلبجة", "قلعة دزة", "دربنديخان", "رانية", "دوكان"
        ));

        // دهوك
        GOVERNORATES_CITIES.put("دهوك", Arrays.asList(
                "دهوك", "زاخو", "عقرة", "عمادية", "سميل", "الشيخان"
        ));

        // الأنبار
        GOVERNORATES_CITIES.put("الأنبار", Arrays.asList(
                "الرمادي", "الفلوجة", "هيت", "حديثة", "القائم", "عانة", 
                "راوة", "الرطبة", "البغدادي"
        ));

        // ديالى
        GOVERNORATES_CITIES.put("ديالى", Arrays.asList(
                "بعقوبة", "المقدادية", "خانقين", "بلدروز", "الخالص", 
                "المدائن", "كفري"
        ));

        // صلاح الدين
        GOVERNORATES_CITIES.put("صلاح الدين", Arrays.asList(
                "تكريت", "سامراء", "بيجي", "الدور", "بلد", "الشرقاط", 
                "الطوز", "سليمان بك"
        ));

        // كركوك
        GOVERNORATES_CITIES.put("كركوك", Arrays.asList(
                "كركوك", "الحويجة", "داقوق", "دبس", "الرياض"
        ));

        // النجف
        GOVERNORATES_CITIES.put("النجف", Arrays.asList(
                "النجف", "الكوفة", "المشخاب", "الحيرة"
        ));

        // كربلاء
        GOVERNORATES_CITIES.put("كربلاء", Arrays.asList(
                "كربلاء", "الهندية", "عين التمر", "الحسينية"
        ));

        // بابل
        GOVERNORATES_CITIES.put("بابل", Arrays.asList(
                "الحلة", "المسيب", "المحاويل", "الهاشمية", "القاسم"
        ));

        // واسط
        GOVERNORATES_CITIES.put("واسط", Arrays.asList(
                "الكوت", "النعمانية", "الصويرة", "العزيزية", "الحي", "بدرة"
        ));

        // ميسان
        GOVERNORATES_CITIES.put("ميسان", Arrays.asList(
                "العمارة", "المجر الكبير", "قلعة صالح", "الميمونة", "علي الغربي"
        ));

        // ذي قار
        GOVERNORATES_CITIES.put("ذي قار", Arrays.asList(
                "الناصرية", "الشطرة", "الرفاعي", "قلعة سكر", "الجبايش", "سوق الشيوخ"
        ));

        // القادسية
        GOVERNORATES_CITIES.put("القادسية", Arrays.asList(
                "الديوانية", "عفك", "الشامية", "الحمزة", "غماس", "الدغارة"
        ));

        // المثنى
        GOVERNORATES_CITIES.put("المثنى", Arrays.asList(
                "السماوة", "الرميثة", "الخضر", "الوركاء", "السلمان"
        ));
    }

    /**
     * الحصول على جميع المحافظات
     */
    public static Set<String> getAllGovernorates() {
        return GOVERNORATES_CITIES.keySet();
    }

    /**
     * الحصول على مدن محافظة معينة
     */
    public static List<String> getCitiesByGovernorate(String governorate) {
        return GOVERNORATES_CITIES.getOrDefault(governorate, Collections.emptyList());
    }

    /**
     * التحقق من صحة محافظة
     */
    public static boolean isValidGovernorate(String governorate) {
        return GOVERNORATES_CITIES.containsKey(governorate);
    }

    /**
     * التحقق من صحة مدينة في محافظة
     */
    public static boolean isValidCity(String governorate, String city) {
        List<String> cities = GOVERNORATES_CITIES.get(governorate);
        return cities != null && cities.contains(city);
    }
}
