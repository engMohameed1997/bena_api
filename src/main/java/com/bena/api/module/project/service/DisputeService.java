package com.bena.api.module.project.service;

import com.bena.api.module.project.entity.Dispute;
import com.bena.api.module.project.entity.Project;
import com.bena.api.module.project.repository.DisputeRepository;
import com.bena.api.module.project.repository.ProjectRepository;
import com.bena.api.module.user.entity.User;
import com.bena.api.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DisputeService {

    private final DisputeRepository disputeRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final EscrowService escrowService;
    private final ProjectNotificationService notificationService;

    @Transactional
    public Dispute createDispute(UUID projectId, UUID raisedById, String title, String description,
                                 Dispute.DisputeType disputeType, String evidenceUrls) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("المشروع غير موجود"));

        User raisedBy = userRepository.findById(raisedById)
                .orElseThrow(() -> new RuntimeException("المستخدم غير موجود"));

        User against;
        if (project.getClient().getId().equals(raisedById)) {
            against = project.getProvider();
        } else if (project.getProvider().getId().equals(raisedById)) {
            against = project.getClient();
        } else {
            throw new RuntimeException("ليس لديك صلاحية رفع نزاع على هذا المشروع");
        }

        Dispute dispute = Dispute.builder()
                .project(project)
                .raisedBy(raisedBy)
                .against(against)
                .title(title)
                .description(description)
                .disputeType(disputeType)
                .evidenceUrls(evidenceUrls)
                .status(Dispute.DisputeStatus.OPEN)
                .paymentHeld(true)
                .build();

        dispute = disputeRepository.save(dispute);

        project.setStatus(Project.ProjectStatus.DISPUTED);
        projectRepository.save(project);

        return dispute;
    }

    @Transactional(readOnly = true)
    public Dispute getDisputeById(UUID disputeId) {
        return disputeRepository.findById(disputeId)
                .orElseThrow(() -> new RuntimeException("النزاع غير موجود"));
    }

    @Transactional(readOnly = true)
    public List<Dispute> getProjectDisputes(UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("المشروع غير موجود"));
        return disputeRepository.findByProject(project);
    }

    @Transactional(readOnly = true)
    public Page<Dispute> getUserDisputes(UUID userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("المستخدم غير موجود"));
        return disputeRepository.findByRaisedByOrAgainst(user, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Dispute> getAllDisputes(Pageable pageable) {
        return disputeRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Dispute> getDisputesByStatus(Dispute.DisputeStatus status, Pageable pageable) {
        return disputeRepository.findByStatus(status, pageable);
    }

    @Transactional
    public Dispute assignDisputeToAdmin(UUID disputeId, UUID adminId) {
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new RuntimeException("النزاع غير موجود"));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("المدير غير موجود"));

        dispute.setAssignedAdmin(admin);
        dispute.setStatus(Dispute.DisputeStatus.UNDER_REVIEW);

        return disputeRepository.save(dispute);
    }

    @Transactional
    public Dispute updateDisputeStatus(UUID disputeId, Dispute.DisputeStatus newStatus, String adminNotes) {
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new RuntimeException("النزاع غير موجود"));

        dispute.setStatus(newStatus);
        if (adminNotes != null) {
            dispute.setAdminNotes(adminNotes);
        }

        return disputeRepository.save(dispute);
    }

    @Transactional
    public Dispute resolveDispute(UUID disputeId, Dispute.ResolutionOutcome outcome, 
                                  String resolutionDetails) {
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new RuntimeException("النزاع غير موجود"));

        dispute.setStatus(Dispute.DisputeStatus.RESOLVED);
        dispute.setResolutionOutcome(outcome);
        dispute.setResolutionDetails(resolutionDetails);
        dispute.setResolvedAt(LocalDateTime.now());
        dispute.setPaymentHeld(false);

        dispute = disputeRepository.save(dispute);

        Project project = dispute.getProject();
        if (outcome == Dispute.ResolutionOutcome.FAVOR_CLIENT) {
            project.setStatus(Project.ProjectStatus.CANCELLED);
        } else if (outcome == Dispute.ResolutionOutcome.FAVOR_PROVIDER) {
            project.setStatus(Project.ProjectStatus.IN_PROGRESS);
        } else if (outcome == Dispute.ResolutionOutcome.COMPROMISE) {
            project.setStatus(Project.ProjectStatus.IN_PROGRESS);
        }
        projectRepository.save(project);

        return dispute;
    }

    @Transactional
    public Dispute addEvidenceToDispute(UUID disputeId, String evidenceUrls) {
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new RuntimeException("النزاع غير موجود"));

        String currentEvidence = dispute.getEvidenceUrls();
        if (currentEvidence != null && !currentEvidence.isEmpty()) {
            dispute.setEvidenceUrls(currentEvidence + "," + evidenceUrls);
        } else {
            dispute.setEvidenceUrls(evidenceUrls);
        }

        return disputeRepository.save(dispute);
    }

    @Transactional(readOnly = true)
    public Long getOpenDisputesCount() {
        return disputeRepository.countByStatus(Dispute.DisputeStatus.OPEN);
    }

    @Transactional(readOnly = true)
    public List<Dispute> getAdminDisputes(UUID adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("المدير غير موجود"));
        return disputeRepository.findByAssignedAdmin(admin);
    }
}
