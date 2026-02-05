package com.bena.api.module.project.dto;

import com.bena.api.module.project.entity.Dispute;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class DisputeCreateRequest {

    @NotNull(message = "معرف المشروع مطلوب")
    private UUID projectId;

    @NotBlank(message = "عنوان النزاع مطلوب")
    @Size(max = 200, message = "عنوان النزاع يجب أن لا يتجاوز 200 حرف")
    private String title;

    @NotBlank(message = "وصف النزاع مطلوب")
    @Size(max = 5000, message = "وصف النزاع يجب أن لا يتجاوز 5000 حرف")
    private String description;

    @NotNull(message = "نوع النزاع مطلوب")
    private Dispute.DisputeType disputeType;

    private String evidenceUrls;
}
