package com.bena.api.module.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetPasswordRequest {
    
    @NotBlank(message = "رمز التحقق مطلوب")
    private String token;
    
    @NotBlank(message = "البريد الإلكتروني مطلوب")
    private String email;

    @NotBlank(message = "كلمة المرور الجديدة مطلوبة")
    @Size(min = 8, message = "كلمة المرور يجب أن تكون 8 أحرف على الأقل")
    private String newPassword;
    
    @NotBlank(message = "تأكيد كلمة المرور مطلوب")
    private String confirmPassword;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
