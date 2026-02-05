package com.bena.api.module.user.dto;

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
public class ChangePasswordRequest {

    @NotBlank(message = "كلمة المرور الحالية مطلوبة")
    private String currentPassword;

    @NotBlank(message = "كلمة المرور الجديدة مطلوبة")
    @Size(min = 8, message = "كلمة المرور يجب أن تكون 8 أحرف على الأقل")
    private String newPassword;

    @NotBlank(message = "تأكيد كلمة المرور مطلوب")
    private String confirmPassword;
}
