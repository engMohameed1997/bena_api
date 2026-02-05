package com.bena.api.module.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProjectRejectRequest {

    @NotBlank(message = "سبب الرفض مطلوب")
    @Size(max = 1000, message = "سبب الرفض يجب أن لا يتجاوز 1000 حرف")
    private String reason;
}
