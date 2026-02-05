package com.bena.api.module.userprogress.service;

import com.bena.api.module.buildingsteps.service.BuildingStepsService;
import com.bena.api.module.user.entity.User;
import com.bena.api.module.userprogress.dto.*;
import com.bena.api.module.userprogress.entity.UserStepProgress;
import com.bena.api.module.userprogress.repository.UserStepProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProgressService {

    private final UserStepProgressRepository repository;
    private final BuildingStepsService buildingStepsService;

    /**
     * جلب جميع تقدم المستخدم
     */
    public List<UserStepProgressDto> getAllProgress(UUID userId) {
        return repository.findByUserIdOrderByStepIdAsc(userId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * جلب تقدم خطوة معينة
     */
    public UserStepProgressDto getStepProgress(UUID userId, Long stepId) {
        return repository.findByUserIdAndStepId(userId, stepId)
                .map(this::toDto)
                .orElse(null);
    }

    /**
     * جلب ملخص التقدم
     */
    public UserProgressSummaryDto getProgressSummary(UUID userId) {
        long totalSteps = buildingStepsService.getTotalStepsCount();
        long completedSteps = repository.countCompletedByUserId(userId);
        BigDecimal totalCost = repository.getTotalCostByUserId(userId);
        long stepsWithNotes = repository.countWithNotesByUserId(userId);
        long stepsWithCosts = repository.countWithCostByUserId(userId);

        double progressPercentage = totalSteps > 0 
                ? (completedSteps * 100.0 / totalSteps) 
                : 0;

        return UserProgressSummaryDto.builder()
                .totalSteps((int) totalSteps)
                .completedSteps((int) completedSteps)
                .progressPercentage(progressPercentage)
                .totalCost(totalCost != null ? totalCost : BigDecimal.ZERO)
                .stepsWithNotes((int) stepsWithNotes)
                .stepsWithCosts((int) stepsWithCosts)
                .build();
    }

    /**
     * تبديل حالة إكمال الخطوة
     */
    @Transactional
    public UserStepProgressDto toggleStepCompletion(User user, StepProgressRequest request) {
        UserStepProgress progress = repository
                .findByUserIdAndStepId(user.getId(), request.getStepId())
                .orElseGet(() -> UserStepProgress.builder()
                        .user(user)
                        .stepId(request.getStepId())
                        .stepTitle(request.getStepTitle())
                        .build());

        progress.setIsCompleted(request.getIsCompleted());
        progress.setCompletedAt(request.getIsCompleted() ? OffsetDateTime.now() : null);

        return toDto(repository.save(progress));
    }

    /**
     * حفظ ملاحظة لخطوة
     */
    @Transactional
    public UserStepProgressDto saveNote(User user, StepProgressRequest request) {
        UserStepProgress progress = repository
                .findByUserIdAndStepId(user.getId(), request.getStepId())
                .orElseGet(() -> UserStepProgress.builder()
                        .user(user)
                        .stepId(request.getStepId())
                        .stepTitle(request.getStepTitle())
                        .build());

        progress.setNotes(request.getNotes());

        return toDto(repository.save(progress));
    }

    /**
     * حفظ تكلفة لخطوة
     */
    @Transactional
    public UserStepProgressDto saveCost(User user, StepProgressRequest request) {
        UserStepProgress progress = repository
                .findByUserIdAndStepId(user.getId(), request.getStepId())
                .orElseGet(() -> UserStepProgress.builder()
                        .user(user)
                        .stepId(request.getStepId())
                        .stepTitle(request.getStepTitle())
                        .build());

        progress.setActualCost(request.getActualCost());

        return toDto(repository.save(progress));
    }

    /**
     * تحديث ملاحظة وتكلفة معاً
     */
    @Transactional
    public UserStepProgressDto updateNoteAndCost(User user, StepProgressRequest request) {
        UserStepProgress progress = repository
                .findByUserIdAndStepId(user.getId(), request.getStepId())
                .orElseGet(() -> UserStepProgress.builder()
                        .user(user)
                        .stepId(request.getStepId())
                        .stepTitle(request.getStepTitle())
                        .build());

        if (request.getNotes() != null) {
            progress.setNotes(request.getNotes());
        }
        if (request.getActualCost() != null) {
            progress.setActualCost(request.getActualCost());
        }

        return toDto(repository.save(progress));
    }

    private UserStepProgressDto toDto(UserStepProgress entity) {
        return UserStepProgressDto.builder()
                .id(entity.getId())
                .stepId(entity.getStepId())
                .stepTitle(entity.getStepTitle())
                .isCompleted(entity.getIsCompleted())
                .completedAt(entity.getCompletedAt())
                .notes(entity.getNotes())
                .actualCost(entity.getActualCost())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
