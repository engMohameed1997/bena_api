package com.bena.api.module.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class ForgotPasswordRequest {
    
    // يمكن أن يكون بريد إلكتروني أو رقم هاتف
    @NotBlank(message = "البريد الإلكتروني أو رقم الهاتف مطلوب")
    private String identifier;
    
    // نوع المعرف: EMAIL أو PHONE (اختياري - يتم الكشف تلقائياً)
    private String type;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    // للتوافق مع الكود القديم
    public String getEmail() {
        return "EMAIL".equalsIgnoreCase(type) ? identifier : null;
    }
    
    public String getPhone() {
        return "PHONE".equalsIgnoreCase(type) ? identifier : null;
    }
    
    public boolean isEmail() {
        return "EMAIL".equalsIgnoreCase(type);
    }
    
    public boolean isPhone() {
        return "PHONE".equalsIgnoreCase(type);
    }
}
