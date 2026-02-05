package com.bena.api.module.project.service;

import com.bena.api.module.project.dto.ProjectCreateRequest;
import com.bena.api.module.project.dto.ProjectResponse;
import com.bena.api.module.project.entity.Project;
import com.bena.api.module.project.repository.ProjectRepository;
import com.bena.api.module.user.entity.User;
import com.bena.api.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectNotificationService notificationService;

    @Transactional
    public ProjectResponse createProject(UUID clientId, ProjectCreateRequest request) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("العميل غير موجود"));

        User provider = userRepository.findById(request.getProviderId())
                .orElseThrow(() -> new RuntimeException("المختص غير موجود"));

        Project project = Project.builder()
                .client(client)
                .provider(provider)
                .title(request.getTitle())
                .description(request.getDescription())
                .projectType(request.getProjectType())
                .totalBudget(request.getTotalBudget())
                .platformCommissionPercentage(request.getPlatformCommissionPercentage())
                .locationCity(request.getLocationCity())
                .locationArea(request.getLocationArea())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .expectedEndDate(request.getExpectedEndDate())
                .status(Project.ProjectStatus.PENDING)
                .build();

        project.calculateCommissionAndProviderAmount();
        project = projectRepository.save(project);

        notificationService.notifyProjectCreated(project);

        return mapToResponse(project);
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("المشروع غير موجود"));
        return mapToResponse(project);
    }

    @Transactional(readOnly = true)
    public Page<ProjectResponse> getClientProjects(UUID clientId, Pageable pageable) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("العميل غير موجود"));
        return projectRepository.findByClient(client, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProjectResponse> getProviderProjects(UUID providerId, Pageable pageable) {
        log.info("═══════════════════════════════════════════════════════");
        log.info("Projects: 📂 FETCHING PROVIDER PROJECTS");
        log.info("Projects: 👤 Provider ID: {}", providerId);
        log.info("Projects: 📄 Page: {}, Size: {}", pageable.getPageNumber(), pageable.getPageSize());

        User provider = userRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("المختص غير موجود"));

        log.info("Projects: ✅ Provider found: {} ({})", provider.getFullName(), provider.getEmail());
        log.info("Projects: 🎭 Provider role: {}", provider.getRole());

        Page<Project> projects = projectRepository.findByProvider(provider, pageable);

        log.info("Projects: 📊 Found {} projects for provider", projects.getTotalElements());
        log.info("Projects: 📄 Current page: {}/{}", projects.getNumber() + 1, projects.getTotalPages());

        if (projects.isEmpty()) {
            log.warn("Projects: ⚠️ No projects found for provider {}", providerId);
        } else {
            log.info("Projects: 📋 Project IDs:");
            projects.getContent().forEach(p ->
                log.info("Projects:   - {} (Status: {}, Client: {})",
                    p.getId(), p.getStatus(), p.getClient().getFullName())
            );
        }

        log.info("═══════════════════════════════════════════════════════");

        return projects.map(this::mapToResponse);
    }

    @Transactional
    public ProjectResponse updateProjectStatus(UUID projectId, Project.ProjectStatus newStatus) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("المشروع غير موجود"));

        project.setStatus(newStatus);
        
        if (newStatus == Project.ProjectStatus.IN_PROGRESS && project.getStartDate() == null) {
            project.setStartDate(java.time.LocalDateTime.now());
        }
        
        if (newStatus == Project.ProjectStatus.COMPLETED && project.getActualEndDate() == null) {
            project.setActualEndDate(java.time.LocalDateTime.now());
        }

        project = projectRepository.save(project);
        notificationService.notifyProjectStatusChanged(project);

        return mapToResponse(project);
    }

    @Transactional
    public ProjectResponse rejectProject(UUID projectId, String reason) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("المشروع غير موجود"));
        
        if (project.getStatus() != Project.ProjectStatus.PENDING) {
            throw new RuntimeException("لا يمكن رفض المشروع في حالته الحالية");
        }

        project.setStatus(Project.ProjectStatus.REJECTED);
        project.setRejectionReason(reason);
        project = projectRepository.save(project);
        
        // TODO: Notify client about rejection with reason
        notificationService.notifyProjectStatusChanged(project);

        return mapToResponse(project);
    }

    private ProjectResponse mapToResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .clientId(project.getClient().getId())
                .clientName(project.getClient().getFullName())
                .providerId(project.getProvider().getId())
                .providerName(project.getProvider().getFullName())
                .title(project.getTitle())
                .description(project.getDescription())
                .projectType(project.getProjectType())
                .status(project.getStatus())
                .totalBudget(project.getTotalBudget())
                .platformCommissionPercentage(project.getPlatformCommissionPercentage())
                .platformCommissionAmount(project.getPlatformCommissionAmount())
                .providerAmount(project.getProviderAmount())
                .rejectionReason(project.getRejectionReason())
                .locationCity(project.getLocationCity())
                .locationArea(project.getLocationArea())
                .latitude(project.getLatitude())
                .longitude(project.getLongitude())
                .startDate(project.getStartDate())
                .expectedEndDate(project.getExpectedEndDate())
                .actualEndDate(project.getActualEndDate())
                .clientRating(project.getClientRating())
                .clientReview(project.getClientReview())
                .providerRating(project.getProviderRating())
                .providerReview(project.getProviderReview())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }
}
