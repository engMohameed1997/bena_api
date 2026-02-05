package com.bena.api.module.user.enums;

import lombok.Getter;

@Getter
public enum VerificationStatus {
    PENDING("قيد المراجعة"),
    APPROVED("موافق عليه"),
    REJECTED("مرفوض"),
    RESUBMITTED("تم إعادة التقديم");

    private final String arabicName;

    VerificationStatus(String arabicName) {
        this.arabicName = arabicName;
    }
}
