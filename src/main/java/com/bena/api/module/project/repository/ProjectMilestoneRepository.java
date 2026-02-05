package com.bena.api.module.project.repository;

import com.bena.api.module.project.entity.Project;
import com.bena.api.module.project.entity.ProjectMilestone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectMilestoneRepository extends JpaRepository<ProjectMilestone, UUID> {

    List<ProjectMilestone> findByProjectOrderByMilestoneOrderAsc(Project project);

    List<ProjectMilestone> findByProjectAndStatus(Project project, ProjectMilestone.MilestoneStatus status);

    Long countByProject(Project project);

    Long countByProjectAndStatus(Project project, ProjectMilestone.MilestoneStatus status);

    List<ProjectMilestone> findByProjectAndClientApproved(Project project, Boolean clientApproved);

    List<ProjectMilestone> findByProjectAndPaymentReleased(Project project, Boolean paymentReleased);
}
