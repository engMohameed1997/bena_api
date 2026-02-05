package com.bena.api.module.user.dto;

import lombok.Data;

@Data
public class ProfileCompletionRequest {
    private String governorate;
    private String city;
    private String documentType;
    private String documentNumber;
    // سيتم رفع الملف بشكل منفصل عبر MultipartFile
}
