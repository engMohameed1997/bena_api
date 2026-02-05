package com.bena.api.module.userprogress.controller;

import com.bena.api.common.dto.ApiResponse;
import com.bena.api.module.user.entity.User;
import com.bena.api.module.userprogress.dto.*;
import com.bena.api.module.userprogress.service.UserProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/user-progress")
@RequiredArgsConstructor
public class UserProgressController {

    private final UserProgressService service;

    /**
     * جلب جميع تقدم المستخدم
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserStepProgressDto>>> getAllProgress(
            @AuthenticationPrincipal User user) {
        List<UserStepProgressDto> progress = service.getAllProgress(user.getId());
        return ResponseEntity.ok(ApiResponse.success(progress));
    }

    /**
     * جلب تقدم خطوة معينة
     */
    @GetMapping("/step/{stepId}")
    public ResponseEntity<ApiResponse<UserStepProgressDto>> getStepProgress(
            @AuthenticationPrincipal User user,
            @PathVariable Long stepId) {
        UserStepProgressDto progress = service.getStepProgress(user.getId(), stepId);
        if (progress == null) {
            return ResponseEntity.ok(ApiResponse.success(null));
        }
        return ResponseEntity.ok(ApiResponse.success(progress));
    }

    /**
     * جلب ملخص التقدم
     */
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<UserProgressSummaryDto>> getProgressSummary(
            @AuthenticationPrincipal User user) {
        UserProgressSummaryDto summary = service.getProgressSummary(user.getId());
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    /**
     * تبديل حالة إكمال الخطوة
     */
    @PostMapping("/toggle")
    public ResponseEntity<ApiResponse<UserStepProgressDto>> toggleStepCompletion(
            @AuthenticationPrincipal User user,
            @RequestBody StepProgressRequest request) {
        UserStepProgressDto result = service.toggleStepCompletion(user, request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * حفظ ملاحظة لخطوة
     */
    @PostMapping("/note")
    public ResponseEntity<ApiResponse<UserStepProgressDto>> saveNote(
            @AuthenticationPrincipal User user,
            @RequestBody StepProgressRequest request) {
        UserStepProgressDto result = service.saveNote(user, request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * حفظ تكلفة لخطوة
     */
    @PostMapping("/cost")
    public ResponseEntity<ApiResponse<UserStepProgressDto>> saveCost(
            @AuthenticationPrincipal User user,
            @RequestBody StepProgressRequest request) {
        UserStepProgressDto result = service.saveCost(user, request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * تحديث ملاحظة وتكلفة معاً
     */
    @PostMapping("/update")
    public ResponseEntity<ApiResponse<UserStepProgressDto>> updateNoteAndCost(
            @AuthenticationPrincipal User user,
            @RequestBody StepProgressRequest request) {
        UserStepProgressDto result = service.updateNoteAndCost(user, request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
