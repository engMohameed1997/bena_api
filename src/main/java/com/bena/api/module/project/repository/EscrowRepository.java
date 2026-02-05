package com.bena.api.module.project.repository;

import com.bena.api.module.project.entity.Escrow;
import com.bena.api.module.project.entity.Project;
import com.bena.api.module.project.entity.ProjectMilestone;
import com.bena.api.module.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface EscrowRepository extends JpaRepository<Escrow, UUID> {

    List<Escrow> findByProject(Project project);

    List<Escrow> findByMilestone(ProjectMilestone milestone);

    List<Escrow> findByStatus(Escrow.EscrowStatus status);

    List<Escrow> findByProjectAndStatus(Project project, Escrow.EscrowStatus status);

    List<Escrow> findByPayer(User payer);

    List<Escrow> findByPayee(User payee);

    @Query("SELECT e FROM Escrow e WHERE e.status = :status AND e.autoReleaseEnabled = true AND e.releaseScheduledAt <= :now")
    List<Escrow> findAutoReleaseEligible(@Param("status") Escrow.EscrowStatus status, @Param("now") LocalDateTime now);

    @Query("SELECT SUM(e.heldAmount) FROM Escrow e WHERE e.status = :status")
    BigDecimal sumHeldAmountByStatus(@Param("status") Escrow.EscrowStatus status);

    @Query("SELECT SUM(e.releasedAmount) FROM Escrow e WHERE e.project = :project")
    BigDecimal sumReleasedAmountByProject(@Param("project") Project project);

    Long countByStatus(Escrow.EscrowStatus status);
}
