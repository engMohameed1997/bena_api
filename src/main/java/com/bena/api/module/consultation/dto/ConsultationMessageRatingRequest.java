package com.bena.api.module.consultation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationMessageRatingRequest {

    private Integer rating;
    private Boolean helpful;
}
