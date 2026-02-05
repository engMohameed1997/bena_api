package com.bena.api.module.user.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    USER("مستخدم"),
    CLIENT("عميل"),
    WORKER("خلف/عامل"),
    CONTRACTOR("مقاول"),
    ENGINEER("مهندس"),
    DESIGNER("مصمم"),
    ADMIN("مدير"),
    SUPER_ADMIN("مدير عام");

    private final String arabicName;

    UserRole(String arabicName) {
        this.arabicName = arabicName;
    }
}
