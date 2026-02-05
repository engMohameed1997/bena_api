package com.bena.api.module.project.controller;

import com.bena.api.module.project.dto.ProjectCreateRequest;
import com.bena.api.module.project.dto.ProjectResponse;
import com.bena.api.module.project.entity.Project;
import com.bena.api.module.project.service.ProjectService;
import com.bena.api.module.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ProjectCreateRequest request) {
        ProjectResponse response = projectService.createProject(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable UUID projectId) {
        ProjectResponse response = projectService.getProjectById(projectId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-projects")
    public ResponseEntity<Page<ProjectResponse>> getMyProjects(
            @AuthenticationPrincipal User user,
            Pageable pageable) {
        Page<ProjectResponse> projects = projectService.getClientProjects(user.getId(), pageable);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/provider-projects")
    public ResponseEntity<Page<ProjectResponse>> getProviderProjects(
            @AuthenticationPrincipal User user,
            Pageable pageable) {
        Page<ProjectResponse> projects = projectService.getProviderProjects(user.getId(), pageable);
        return ResponseEntity.ok(projects);
    }

    @PatchMapping("/{projectId}/status")
    public ResponseEntity<ProjectResponse> updateProjectStatus(
            @PathVariable UUID projectId,
            @RequestParam Project.ProjectStatus status) {
        ProjectResponse response = projectService.updateProjectStatus(projectId, status);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{projectId}/reject")
    public ResponseEntity<ProjectResponse> rejectProject(
            @PathVariable UUID projectId,
            @Valid @RequestBody com.bena.api.module.project.dto.ProjectRejectRequest request) {
        ProjectResponse response = projectService.rejectProject(projectId, request.getReason());
        return ResponseEntity.ok(response);
    }
}
