package com.bena.api.module.project.service;

import com.bena.api.module.project.entity.Payment;
import com.bena.api.module.project.entity.Project;
import com.bena.api.module.project.entity.ProjectMilestone;
import com.bena.api.module.project.repository.ProjectPaymentRepository;
import com.bena.api.module.project.repository.ProjectMilestoneRepository;
import com.bena.api.module.project.repository.ProjectRepository;
import com.bena.api.module.user.entity.User;
import com.bena.api.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectPaymentService {

    private final ProjectPaymentRepository paymentRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMilestoneRepository milestoneRepository;
    private final UserRepository userRepository;
    private final ProjectNotificationService notificationService;

    @Transactional
    public Payment createPayment(UUID projectId, UUID milestoneId, UUID payerId, UUID payeeId,
                                 BigDecimal amount, Payment.PaymentType paymentType) {
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

        BigDecimal platformFee = amount.multiply(project.getPlatformCommissionPercentage())
                .divide(new BigDecimal("100"));

        Payment payment = Payment.builder()
                .project(project)
                .milestone(milestone)
                .payer(payer)
                .payee(payee)
                .amount(amount)
                .platformFee(platformFee)
                .paymentType(paymentType)
                .status(Payment.PaymentStatus.PENDING)
                .build();

        payment.calculateNetAmount();
        payment = paymentRepository.save(payment);

        return payment;
    }

    @Transactional(readOnly = true)
    public Payment getPaymentById(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("الدفعة غير موجودة"));
    }

    @Transactional(readOnly = true)
    public List<Payment> getProjectPayments(UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("المشروع غير موجود"));
        return paymentRepository.findByProject(project);
    }

    @Transactional(readOnly = true)
    public Page<Payment> getUserPayments(UUID userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("المستخدم غير موجود"));
        return paymentRepository.findByPayer(user, pageable);
    }

    @Transactional
    public Payment processPayment(UUID paymentId, String transactionId, String paymentGateway,
                                  Payment.PaymentMethod paymentMethod) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("الدفعة غير موجودة"));

        payment.setStatus(Payment.PaymentStatus.PROCESSING);
        payment.setTransactionId(transactionId);
        payment.setPaymentGateway(paymentGateway);
        payment.setPaymentMethod(paymentMethod);

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment completePayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("الدفعة غير موجودة"));

        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        payment.setPaymentDate(LocalDateTime.now());

        payment = paymentRepository.save(payment);

        return payment;
    }

    @Transactional
    public Payment refundPayment(UUID paymentId, BigDecimal refundAmount, String refundReason) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("الدفعة غير موجودة"));

        if (payment.getStatus() != Payment.PaymentStatus.COMPLETED) {
            throw new RuntimeException("لا يمكن استرجاع دفعة غير مكتملة");
        }

        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        payment.setRefundAmount(refundAmount);
        payment.setRefundDate(LocalDateTime.now());
        payment.setRefundReason(refundReason);

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment failPayment(UUID paymentId, String reason) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("الدفعة غير موجودة"));

        payment.setStatus(Payment.PaymentStatus.FAILED);
        payment.setNotes(reason);

        return paymentRepository.save(payment);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalPaidAmount(UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("المشروع غير موجود"));
        return paymentRepository.sumAmountByProjectAndStatus(project, Payment.PaymentStatus.COMPLETED);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalPlatformFees() {
        return paymentRepository.sumPlatformFeeByStatus(Payment.PaymentStatus.COMPLETED);
    }
}
