package com.bena.api.module.project.repository;

import com.bena.api.module.project.entity.Dispute;
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
public interface DisputeRepository extends JpaRepository<Dispute, UUID> {

    List<Dispute> findByProject(Project project);

    Page<Dispute> findByRaisedBy(User raisedBy, Pageable pageable);

    Page<Dispute> findByAgainst(User against, Pageable pageable);

    Page<Dispute> findByStatus(Dispute.DisputeStatus status, Pageable pageable);

    List<Dispute> findByAssignedAdmin(User assignedAdmin);

    @Query("SELECT d FROM Dispute d WHERE (d.raisedBy = :user OR d.against = :user)")
    Page<Dispute> findByRaisedByOrAgainst(@Param("user") User user, Pageable pageable);

    @Query("SELECT d FROM Dispute d WHERE (d.raisedBy = :user OR d.against = :user) AND d.status = :status")
    Page<Dispute> findByRaisedByOrAgainstAndStatus(@Param("user") User user, @Param("status") Dispute.DisputeStatus status, Pageable pageable);

    Long countByStatus(Dispute.DisputeStatus status);

    Long countByProject(Project project);

    Long countByAssignedAdmin(User assignedAdmin);
}
