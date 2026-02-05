package com.bena.api.module.offers.entity;

import lombok.Getter;

/**
 * أنواع العروض المتاحة
 */
@Getter
public enum OfferType {
    SKELETON_BUILD("بناء هيكل عظم"),
    TURNKEY_BUILD("بناء تسليم مفتاح"),
    ENGINEERING_CONSULT("استشارات هندسية"),
    ARCHITECTURAL_DESIGN("تصميم معماري");

    private final String arabicName;

    OfferType(String arabicName) {
        this.arabicName = arabicName;
    }
}
