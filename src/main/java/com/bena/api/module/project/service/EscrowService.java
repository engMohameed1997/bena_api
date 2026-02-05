package com.bena.api.module.project.service;

import com.bena.api.module.project.entity.Escrow;
import com.bena.api.module.project.entity.Project;
import com.bena.api.module.project.entity.ProjectMilestone;
import com.bena.api.module.project.repository.EscrowRepository;
import com.bena.api.module.project.repository.ProjectMilestoneRepository;
import com.bena.api.module.project.repository.ProjectRepository;
import com.bena.api.module.user.entity.User;
import com.bena.api.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EscrowService {

    private final EscrowRepository escrowRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMilestoneRepository milestoneRepository;
    private final UserRepository userRepository;
    private final ProjectNotificationService notificationService;

    @Transactional
    public Escrow createEscrow(UUID projectId, UUID milestoneId, UUID payerId, UUID payeeId,
                              BigDecimal amount, Integer autoReleaseDays) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("المشروع غير موجود"));

        User payer = userRepository.findById(payerId)
                .orElseThrow(() -> new RuntimeException("الدافع غير موجود"));

        User payee = userRepository.findById(payeeId)
                .orElseThrow(() -> new RuntimeException("المستلم غير موجود"));

        ProjectMilestone milestone = null;
        if (milestoneId != null) {
            milestone = milestoneRepository.findById(milestoneId)
                    .orElseThrow(() -> new RuntimeException("المرحلة غير موجودة"));
        }

        LocalDateTime releaseScheduledAt = LocalDateTime.now().plusDays(autoReleaseDays != null ? autoReleaseDays : 7);

        Escrow escrow = Escrow.builder()
                .project(project)
                .milestone(milestone)
                .payer(payer)
                .payee(payee)
                .amount(amount)
                .heldAmount(amount)
                .status(Escrow.EscrowStatus.HELD)
                .heldAt(LocalDateTime.now())
                .releaseScheduledAt(releaseScheduledAt)
                .autoReleaseEnabled(true)
                .autoReleaseDays(autoReleaseDays != null ? autoReleaseDays : 7)
                .build();

        return escrowRepository.save(escrow);
    }

    @Transactional(readOnly = true)
    public Escrow getEscrowById(UUID escrowId) {
        return escrowRepository.findById(escrowId)
                .orElseThrow(() -> new RuntimeException("معاملة Escrow غير موجودة"));
    }

    @Transactional(readOnly = true)
    public List<Escrow> getProjectEscrows(UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("المشروع غير موجود"));
        return escrowRepository.findByProject(project);
    }

    @Transactional
    public Escrow releaseEscrow(UUID escrowId, BigDecimal releaseAmount, String releaseReason) {
        Escrow escrow = escrowRepository.findById(escrowId)
                .orElseThrow(() -> new RuntimeException("معاملة Escrow غير موجودة"));

        if (!escrow.canRelease(releaseAmount)) {
            throw new RuntimeException("المبلغ المطلوب إطلاقه أكبر من المبلغ المتاح");
        }

        escrow.setReleasedAmount(escrow.getReleasedAmount().add(releaseAmount));
        escrow.setReleaseReason(releaseReason);

        if (escrow.getRemainingAmount().compareTo(BigDecimal.ZERO) == 0) {
            escrow.setStatus(Escrow.EscrowStatus.RELEASED);
            escrow.setReleasedAt(LocalDateTime.now());
        } else {
            escrow.setStatus(Escrow.EscrowStatus.PARTIALLY_RELEASED);
        }

        escrow = escrowRepository.save(escrow);

        return escrow;
    }

    @Transactional
    public Escrow refundEscrow(UUID escrowId, BigDecimal refundAmount, String refundReason) {
        Escrow escrow = escrowRepository.findById(escrowId)
                .orElseThrow(() -> new RuntimeException("معاملة Escrow غير موجودة"));

        if (!escrow.canRelease(refundAmount)) {
            throw new RuntimeException("المبلغ المطلوب استرجاعه أكبر من المبلغ المتاح");
        }

        escrow.setRefundedAmount(escrow.getRefundedAmount().add(refundAmount));
        escrow.setRefundReason(refundReason);
        escrow.setStatus(Escrow.EscrowStatus.REFUNDED);
        escrow.setRefundedAt(LocalDateTime.now());

        return escrowRepository.save(escrow);
    }

    @Transactional
    public Escrow holdEscrowForDispute(UUID escrowId) {
        Escrow escrow = escrowRepository.findById(escrowId)
                .orElseThrow(() -> new RuntimeException("معاملة Escrow غير موجودة"));

        escrow.setStatus(Escrow.EscrowStatus.DISPUTED);
        escrow.setAutoReleaseEnabled(false);

        return escrowRepository.save(escrow);
    }

    @Transactional
    public void processAutoReleases() {
        List<Escrow> eligibleEscrows = escrowRepository.findAutoReleaseEligible(
                Escrow.EscrowStatus.HELD, LocalDateTime.now());

        for (Escrow escrow : eligibleEscrows) {
            try {
                releaseEscrow(escrow.getId(), escrow.getRemainingAmount(), 
                             "إطلاق تلقائي بعد انتهاء المدة المحددة");
            } catch (Exception e) {
                // Log error
            }
        }
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalHeldAmount() {
        return escrowRepository.sumHeldAmountByStatus(Escrow.EscrowStatus.HELD);
    }

    @Transactional(readOnly = true)
    public BigDecimal getProjectReleasedAmount(UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("المشروع غير موجود"));
        return escrowRepository.sumReleasedAmountByProject(project);
    }
}
