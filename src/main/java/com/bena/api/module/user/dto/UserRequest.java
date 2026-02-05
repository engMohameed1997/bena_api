package com.bena.api.module.user.dto;

import com.bena.api.module.user.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {
    
    @NotBlank(message = "الاسم الكامل مطلوب")
    private String fullName;
    
    @NotBlank(message = "البريد الإلكتروني مطلوب")
    @Email(message = "البريد الإلكتروني غير صحيح")
    private String email;
    
    private String phone;
    
    @NotBlank(message = "كلمة المرور مطلوبة")
    @Size(min = 8, message = "كلمة المرور يجب أن تكون 8 أحرف على الأقل")
    private String password;
    
    private String governorate;
    private String city;
    
    /**
     * الدور: USER أو WORKER (الأدمن لا يمكن إنشاؤه من هنا)
     * Default: USER
     */
    @NotNull(message = "الدور مطلوب")
    private UserRole role = UserRole.USER;
}
