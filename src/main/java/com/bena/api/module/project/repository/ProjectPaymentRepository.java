package com.bena.api.module.project.repository;

import com.bena.api.module.project.entity.Payment;
import com.bena.api.module.project.entity.Project;
import com.bena.api.module.project.entity.ProjectMilestone;
import com.bena.api.module.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectPaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findByProject(Project project);

    List<Payment> findByMilestone(ProjectMilestone milestone);

    Page<Payment> findByPayer(User payer, Pageable pageable);

    Page<Payment> findByPayee(User payee, Pageable pageable);

    List<Payment> findByStatus(Payment.PaymentStatus status);

    List<Payment> findByProjectAndStatus(Project project, Payment.PaymentStatus status);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.project = :project AND p.status = :status")
    BigDecimal sumAmountByProjectAndStatus(@Param("project") Project project, @Param("status") Payment.PaymentStatus status);

    @Query("SELECT SUM(p.platformFee) FROM Payment p WHERE p.status = :status")
    BigDecimal sumPlatformFeeByStatus(@Param("status") Payment.PaymentStatus status);

    Long countByStatus(Payment.PaymentStatus status);

    Long countByProject(Project project);
}
