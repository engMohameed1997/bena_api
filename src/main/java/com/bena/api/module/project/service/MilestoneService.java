package com.bena.api.module.project.service;

import com.bena.api.module.project.dto.MilestoneCreateRequest;
import com.bena.api.module.project.entity.Project;
import com.bena.api.module.project.entity.ProjectMilestone;
import com.bena.api.module.project.repository.ProjectMilestoneRepository;
import com.bena.api.module.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MilestoneService {

    private final ProjectMilestoneRepository milestoneRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public ProjectMilestone createMilestone(UUID projectId, MilestoneCreateRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("المشروع غير موجود"));

        ProjectMilestone milestone = ProjectMilestone.builder()
                .project(project)
                .title(request.getTitle())
                .description(request.getDescription())
                .milestoneOrder(request.getMilestoneOrder())
                .amount(request.getAmount())
                .expectedCompletionDate(request.getExpectedCompletionDate())
                .notes(request.getNotes())
                .status(ProjectMilestone.MilestoneStatus.PENDING)
                .build();

        return milestoneRepository.save(milestone);
    }

    @Transactional(readOnly = true)
    public List<ProjectMilestone> getProjectMilestones(UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("المشروع غير موجود"));
        return milestoneRepository.findByProjectOrderByMilestoneOrderAsc(project);
    }

    @Transactional
    public ProjectMilestone approveMilestone(UUID milestoneId) {
        ProjectMilestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new RuntimeException("المرحلة غير موجودة"));

        milestone.setClientApproved(true);
        milestone.setClientApprovalDate(LocalDateTime.now());
        milestone.setStatus(ProjectMilestone.MilestoneStatus.APPROVED);

        return milestoneRepository.save(milestone);
    }

    @Transactional
    public ProjectMilestone completeMilestone(UUID milestoneId, String workEvidenceUrls) {
        ProjectMilestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new RuntimeException("المرحلة غير موجودة"));

        milestone.setStatus(ProjectMilestone.MilestoneStatus.COMPLETED);
        milestone.setActualCompletionDate(LocalDateTime.now());
        milestone.setWorkEvidenceUrls(workEvidenceUrls);

        return milestoneRepository.save(milestone);
    }
}
