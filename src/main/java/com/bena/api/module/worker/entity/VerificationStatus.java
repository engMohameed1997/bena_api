package com.bena.api.module.worker.entity;

import lombok.Getter;

@Getter
public enum VerificationStatus {
    PENDING("قيد الانتظار"),
    UNDER_REVIEW("قيد المراجعة"),
    // لقبول القيم القديمة المخزنة بصفة APPROVED
    APPROVED("موافق عليه"),
    VERIFIED("موثق"),
    REJECTED("مرفوض"),
    EXPIRED("منتهي الصلاحية");

    private final String arabicName;

    VerificationStatus(String arabicName) {
        this.arabicName = arabicName;
    }
}
