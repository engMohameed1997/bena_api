package com.bena.api.module.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserUpdateRequest {
    
    private String fullName;
    
    @Email(message = "البريد الإلكتروني غير صحيح")
    private String email;
    
    private String phone;
    private String password; // Optional
    private String governorate;
    private String city;
}
