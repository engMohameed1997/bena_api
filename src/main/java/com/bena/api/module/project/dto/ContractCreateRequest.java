package com.bena.api.module.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class ContractCreateRequest {

    @NotNull(message = "معرف المشروع مطلوب")
    private UUID projectId;

    @NotBlank(message = "شروط العقد مطلوبة")
    @Size(max = 10000, message = "شروط العقد يجب أن لا تتجاوز 10000 حرف")
    private String contractTerms;

    @Size(max = 5000, message = "شروط الدفع يجب أن لا تتجاوز 5000 حرف")
    private String paymentTerms;

    @Size(max = 5000, message = "شروط التسليم يجب أن لا تتجاوز 5000 حرف")
    private String deliveryTerms;

    @Size(max = 5000, message = "سياسة الإلغاء يجب أن لا تتجاوز 5000 حرف")
    private String cancellationPolicy;
}
