package com.bena.api.module.project.repository;

import com.bena.api.module.project.entity.Project;
import com.bena.api.module.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    Page<Project> findByClient(User client, Pageable pageable);

    Page<Project> findByProvider(User provider, Pageable pageable);

    Page<Project> findByStatus(Project.ProjectStatus status, Pageable pageable);

    Page<Project> findByClientAndStatus(User client, Project.ProjectStatus status, Pageable pageable);

    Page<Project> findByProviderAndStatus(User provider, Project.ProjectStatus status, Pageable pageable);

    @Query("SELECT p FROM Project p WHERE p.client = :user OR p.provider = :user")
    Page<Project> findByClientOrProvider(@Param("user") User user, Pageable pageable);

    @Query("SELECT p FROM Project p WHERE (p.client = :user OR p.provider = :user) AND p.status = :status")
    Page<Project> findByClientOrProviderAndStatus(@Param("user") User user, @Param("status") Project.ProjectStatus status, Pageable pageable);

    List<Project> findByClientAndStatusIn(User client, List<Project.ProjectStatus> statuses);

    List<Project> findByProviderAndStatusIn(User provider, List<Project.ProjectStatus> statuses);

    Long countByClient(User client);

    Long countByProvider(User provider);

    Long countByStatus(Project.ProjectStatus status);
}
