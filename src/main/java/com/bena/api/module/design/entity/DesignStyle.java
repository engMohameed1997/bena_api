package com.bena.api.module.design.entity;

/**
 * أنماط التصميم المتاحة
 */
public enum DesignStyle {
    CLASSIC("كلاسيك"),
    MODERN("مودرن"),
    CONTEMPORARY("معاصر"),
    MINIMALIST("بسيط"),
    LUXURY("فاخر"),
    TRADITIONAL("تقليدي"),
    INDUSTRIAL("صناعي"),
    SCANDINAVIAN("اسكندنافي");

    private final String arabicName;

    DesignStyle(String arabicName) {
        this.arabicName = arabicName;
    }

    public String getArabicName() {
        return arabicName;
    }
}
