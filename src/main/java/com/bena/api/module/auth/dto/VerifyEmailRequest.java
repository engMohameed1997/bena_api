package com.bena.api.module.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyEmailRequest {
    
    @NotBlank(message = "البريد الإلكتروني مطلوب")
    private String email;

    @NotBlank(message = "رمز التحقق مطلوب")
    private String token;
}
