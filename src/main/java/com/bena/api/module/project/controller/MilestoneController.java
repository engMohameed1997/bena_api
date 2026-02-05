package com.bena.api.module.project.controller;

import com.bena.api.module.project.dto.MilestoneCreateRequest;
import com.bena.api.module.project.entity.ProjectMilestone;
import com.bena.api.module.project.service.MilestoneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/projects/{projectId}/milestones")
@RequiredArgsConstructor
public class MilestoneController {

    private final MilestoneService milestoneService;

    @PostMapping
    public ResponseEntity<ProjectMilestone> createMilestone(
            @PathVariable UUID projectId,
            @Valid @RequestBody MilestoneCreateRequest request) {
        ProjectMilestone milestone = milestoneService.createMilestone(projectId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(milestone);
    }

    @GetMapping
    public ResponseEntity<List<ProjectMilestone>> getProjectMilestones(@PathVariable UUID projectId) {
        List<ProjectMilestone> milestones = milestoneService.getProjectMilestones(projectId);
        return ResponseEntity.ok(milestones);
    }

    @PostMapping("/{milestoneId}/approve")
    public ResponseEntity<ProjectMilestone> approveMilestone(@PathVariable UUID milestoneId) {
        ProjectMilestone milestone = milestoneService.approveMilestone(milestoneId);
        return ResponseEntity.ok(milestone);
    }

    @PostMapping("/{milestoneId}/complete")
    public ResponseEntity<ProjectMilestone> completeMilestone(
            @PathVariable UUID milestoneId,
            @RequestParam(required = false) String workEvidenceUrls) {
        ProjectMilestone milestone = milestoneService.completeMilestone(milestoneId, workEvidenceUrls);
        return ResponseEntity.ok(milestone);
    }
}
