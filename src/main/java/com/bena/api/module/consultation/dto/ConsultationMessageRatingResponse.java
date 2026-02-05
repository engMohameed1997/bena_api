package com.bena.api.module.consultation.dto;

import com.bena.api.module.consultation.entity.ConsultationMessageRating;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConsultationMessageRatingResponse {

    private Long id;
    private Long messageId;
    private Long workerId;
    private Integer rating;
    private Boolean helpful;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static ConsultationMessageRatingResponse from(ConsultationMessageRating r) {
        return ConsultationMessageRatingResponse.builder()
                .id(r.getId())
                .messageId(r.getMessageId())
                .workerId(r.getWorkerId())
                .rating(r.getRating())
                .helpful(r.getHelpful())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}
