package com.bena.api.module.worker.entity;

/**
 * فئات العمال والخلف
 */
public enum WorkerCategory {
    // الفئات الجديدة - نظام الوساطة
    CONTRACTOR("مقاول"),
    ENGINEER("مهندس"),
    DESIGNER("مصمم"),
    
    // الفئات القديمة - خلف بناء
    CERAMIC("سيراميك"),
    PORCELAIN("بورسلين"),
    MARBLE("مرمر"),
    TILES("كاشي"),
    MASON("خلفة بناء"),
    PLASTERER("خلفة لبخ"),
    CARPENTER("نجار"),
    PAINTER("صباغ"),
    BLACKSMITH("حداد"),
    CEMENT_SHOP("متجر صب اسمنت"),
    WOOD_DOORS_SHOP("متجر ابواب خشب"),
    WELDER("حداد لحام"),
    ASPHALT_WORKER("ابو زفت"),
    ROOF_CASTER("ابو صب اسطح"),
    EXCAVATION_EQUIPMENT("اليات حفر"),
    RESCUE_EQUIPMENT("اليات انتشال وانقاذ"),
    HOUSE_CLEANER("عمال تنظيف منزل"),
    ELECTRICIAN("كهربائي"),
    PLUMBER("سباك"),
    AC_TECHNICIAN("فني تكييف"),
    ALUMINUM_WORKER("المنيوم"),
    GYPSUM_WORKER("جبس"),
    OTHER("اخرى");

    private final String arabicName;

    WorkerCategory(String arabicName) {
        this.arabicName = arabicName;
    }

    public String getArabicName() {
        return arabicName;
    }
}
