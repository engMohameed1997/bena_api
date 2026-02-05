package com.bena.api.module.offers.entity;

import lombok.Getter;

/**
 * حالات طلب العرض
 */
@Getter
public enum OfferRequestStatus {
    PENDING("قيد الانتظار"),
    CONTACTED("تم التواصل"),
    ACCEPTED("مقبول"),
    REJECTED("مرفوض"),
    COMPLETED("مكتمل"),
    CANCELLED("ملغي");

    private final String arabicName;

    OfferRequestStatus(String arabicName) {
        this.arabicName = arabicName;
    }
}
