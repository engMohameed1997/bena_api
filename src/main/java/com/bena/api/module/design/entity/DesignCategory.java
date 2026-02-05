package com.bena.api.module.design.entity;

/**
 * أنواع التصاميم المتاحة
 */
public enum DesignCategory {
    MAPS("خرائط", "تصاميم خرائط معمارية"),
    FACADES("واجهات", "تصاميم واجهات المباني"),
    INTERIOR("ديكور داخلي", "تصاميم الديكور الداخلي"),
    BATHROOMS("حمامات", "تصاميم الحمامات"),
    KITCHENS("مطابخ", "تصاميم المطابخ"),
    BEDROOMS("غرف نوم", "تصاميم غرف النوم"),
    LIVING_ROOMS("غرف جلوس", "تصاميم غرف الجلوس");

    private final String arabicName;
    private final String description;

    DesignCategory(String arabicName, String description) {
        this.arabicName = arabicName;
        this.description = description;
    }

    public String getArabicName() {
        return arabicName;
    }

    public String getDescription() {
        return description;
    }
}
