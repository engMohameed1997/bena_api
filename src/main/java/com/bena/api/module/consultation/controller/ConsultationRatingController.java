package com.bena.api.module.consultation.controller;

import com.bena.api.common.dto.ApiResponse;
import com.bena.api.module.consultation.dto.ConsultationMessageRatingRequest;
import com.bena.api.module.consultation.dto.ConsultationMessageRatingResponse;
import com.bena.api.module.consultation.service.ConsultationRatingService;
import com.bena.api.module.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/consultation")
@RequiredArgsConstructor
@Tag(name = "Consultation Ratings", description = "تقييم ردود المختصين في الاستشارات")
public class ConsultationRatingController {

    private final ConsultationRatingService ratingService;

    @PostMapping("/messages/{messageId}/rating")
    @Operation(summary = "تقييم رد مختص", description = "تقييم رسالة رد من المختص داخل المحادثة")
    public ResponseEntity<ApiResponse<ConsultationMessageRatingResponse>> rateMessage(
            @PathVariable Long messageId,
            @RequestBody ConsultationMessageRatingRequest request,
            @AuthenticationPrincipal User principal
    ) {
        try {
            ConsultationMessageRatingResponse res = ratingService.rateMessage(messageId, request, principal);
            return ResponseEntity.ok(ApiResponse.success(res, "تم حفظ التقييم"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("حدث خطأ في الخادم، يرجى المحاولة لاحقاً"));
        }
    }
}
