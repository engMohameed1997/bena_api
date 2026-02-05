package com.bena.api.module.project.dto;

import com.bena.api.module.project.entity.Dispute;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DisputeResolveRequest {

    @NotNull(message = "نتيجة الحل مطلوبة")
    private Dispute.ResolutionOutcome outcome;

    @NotBlank(message = "تفاصيل الحل مطلوبة")
    @Size(max = 5000, message = "تفاصيل الحل يجب أن لا تتجاوز 5000 حرف")
    private String resolutionDetails;
}
