package com.bena.api.module.worker.dto;

import com.bena.api.module.worker.entity.JobRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobRequestDTO {
    private Long id;
    private UUID userId;
    private Long workerId;
    private String workerName;
    private String jobType;
    private String description;
    private String locationCity;
    private String locationArea;
    private Double latitude;
    private Double longitude;
    private BigDecimal budget;
    private JobRequest.JobStatus status;
    private String statusArabic;
    private String workerResponse;
    private BigDecimal workerPriceOffer;
    private List<String> imagesBase64;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public String getStatusArabic() {
        if (status == null) return "";
        return switch (status) {
            case PENDING -> "قيد الانتظار";
            case ACCEPTED -> "مقبول";
            case REJECTED -> "مرفوض";
            case OFFER_SENT -> "تم إرسال عرض سعر";
            case NEGOTIATING -> "قيد التفاوض";
            case IN_PROGRESS -> "قيد التنفيذ";
            case COMPLETED -> "مكتمل";
            case CANCELLED -> "ملغي";
        };
    }
}
