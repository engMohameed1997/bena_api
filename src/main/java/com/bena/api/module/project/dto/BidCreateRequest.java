package com.bena.api.module.project.dto;

import com.bena.api.module.project.entity.Bid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class BidCreateRequest {

    @NotNull(message = "معرف العميل مطلوب")
    private UUID clientId;

    @NotBlank(message = "عنوان العرض مطلوب")
    @Size(max = 200, message = "عنوان العرض يجب أن لا يتجاوز 200 حرف")
    private String title;

    @Size(max = 5000, message = "الوصف يجب أن لا يتجاوز 5000 حرف")
    private String description;

    @NotNull(message = "نوع الخدمة مطلوب")
    private Bid.ServiceType serviceType;

    @NotNull(message = "السعر المعروض مطلوب")
    @DecimalMin(value = "0.01", message = "السعر يجب أن يكون أكبر من صفر")
    private BigDecimal offeredPrice;

    @Min(value = 1, message = "المدة التقديرية يجب أن تكون يوم واحد على الأقل")
    private Integer estimatedDurationDays;

    @Size(max = 5000, message = "تفاصيل العرض يجب أن لا تتجاوز 5000 حرف")
    private String proposalDetails;

    private String locationCity;

    private String locationArea;

    private Double latitude;

    private Double longitude;
}
