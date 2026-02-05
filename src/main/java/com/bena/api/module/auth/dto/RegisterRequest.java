package com.bena.api.module.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "الاسم الكامل مطلوب")
    @Size(min = 2, max = 100, message = "الاسم يجب أن يكون بين 2 و 100 حرف")
    private String fullName;

    @NotBlank(message = "البريد الإلكتروني مطلوب")
    @Email(message = "البريد الإلكتروني غير صالح")
    private String email;

    @Size(min = 10, max = 20, message = "رقم الهاتف يجب أن يكون بين 10 و 20 رقم")
    private String phone;

    @NotBlank(message = "كلمة المرور مطلوبة")
    @Size(min = 8, max = 100, message = "كلمة المرور يجب أن تكون 8 أحرف على الأقل")
    private String password;

    private String role; // الدور: USER, CONTRACTOR, ENGINEER, DESIGNER, WORKER

    private String governorate; // المحافظة

    private String city; // المدينة
}
