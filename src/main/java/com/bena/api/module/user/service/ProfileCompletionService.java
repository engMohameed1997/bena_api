package com.bena.api.module.user.service;

import com.bena.api.module.user.dto.ProfileCompletionRequest;
import com.bena.api.module.user.dto.ProfileStatusResponse;
import com.bena.api.module.user.entity.User;
import com.bena.api.module.user.enums.UserRole;
import com.bena.api.module.user.enums.VerificationStatus;
import com.bena.api.module.user.repository.UserRepository;
import com.bena.api.module.worker.entity.Worker;
import com.bena.api.module.worker.entity.WorkerCategory;
import com.bena.api.module.worker.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileCompletionService {

    private final UserRepository userRepository;
    private final WorkerRepository workerRepository;
    private static final String UPLOAD_DIR = "uploads/documents/";

    /**
     * الحصول على حالة الملف الشخصي للمستخدم
     */
    public ProfileStatusResponse getProfileStatus(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // التحقق من حالة التعليق من Worker
        Boolean isAccountSuspended = false;
        if (isProfessionalRole(user.getRole())) {
            isAccountSuspended = workerRepository.findByUserId(user.getId())
                    .map(worker -> !Boolean.TRUE.equals(worker.getIsActive()))
                    .orElse(false);
        }

        return ProfileStatusResponse.builder()
                .profileCompleted(user.getProfileCompleted())
                .documentVerified(user.getDocumentVerified())
                .verificationStatus(user.getVerificationStatus())
                .governorate(user.getGovernorate())
                .city(user.getCity())
                .documentType(user.getDocumentType())
                .documentUrl(user.getDocumentUrl())
                .rejectionReason(user.getRejectionReason())
                .canUseFullFeatures(canUseFullFeatures(user))
                .isAccountSuspended(isAccountSuspended)
                .build();
    }

    /**
     * إكمال الملف الشخصي برفع الوثيقة
     */
    @Transactional
    public ProfileStatusResponse completeProfile(UUID userId, ProfileCompletionRequest request, MultipartFile documentFile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // حفظ المعلومات الأساسية
        user.setGovernorate(request.getGovernorate());
        user.setCity(request.getCity());
        user.setDocumentType(request.getDocumentType());
        user.setDocumentNumber(request.getDocumentNumber());

        // رفع الوثيقة إذا كانت موجودة
        if (documentFile != null && !documentFile.isEmpty()) {
            String documentUrl = saveDocument(documentFile, userId);
            user.setDocumentUrl(documentUrl);
            user.setProfileCompleted(true);
            user.setVerificationStatus(VerificationStatus.APPROVED);
            user.setDocumentVerified(true);
            user.setVerifiedAt(OffsetDateTime.now());
        } else {
            // الخلفة لا يحتاج وثيقة
            if (user.getRole() == UserRole.WORKER) {
                user.setProfileCompleted(true);
                user.setVerificationStatus(VerificationStatus.APPROVED);
                user.setDocumentVerified(true);
                user.setVerifiedAt(OffsetDateTime.now());
            }
        }

        // المستخدم العادي لا يحتاج تحقق
        if (user.getRole() == UserRole.USER || user.getRole() == UserRole.CLIENT) {
            user.setProfileCompleted(true);
            user.setVerificationStatus(VerificationStatus.APPROVED);
            user.setDocumentVerified(true);
            user.setVerifiedAt(OffsetDateTime.now());
        }

        userRepository.save(user);
        log.info("Profile completed for user: {}", userId);

        // إنشاء/تحديث سجل Worker للمختصين
        if (isProfessionalRole(user.getRole()) && user.getProfileCompleted()) {
            createOrUpdateWorker(user);
        }

        return getProfileStatus(userId);
    }

    /**
     * التحقق إذا كان الدور مختص (مقاول/مهندس/مصمم/خلفة)
     */
    private boolean isProfessionalRole(UserRole role) {
        return role == UserRole.CONTRACTOR || 
               role == UserRole.ENGINEER || 
               role == UserRole.DESIGNER || 
               role == UserRole.WORKER;
    }

    /**
     * تحويل UserRole إلى WorkerCategory
     */
    private WorkerCategory mapRoleToCategory(UserRole role) {
        return switch (role) {
            case CONTRACTOR -> WorkerCategory.CONTRACTOR;
            case ENGINEER -> WorkerCategory.ENGINEER;
            case DESIGNER -> WorkerCategory.DESIGNER;
            case WORKER -> WorkerCategory.MASON; // الخلفة الافتراضي
            default -> WorkerCategory.OTHER;
        };
    }

    /**
     * إنشاء أو تحديث سجل Worker للمختص
     */
    private void createOrUpdateWorker(User user) {
        Worker worker = workerRepository.findByUserId(user.getId())
                .orElse(Worker.builder()
                        .userId(user.getId())
                        .isActive(true)
                        .isVerified(true)
                        .build());

        // تحديث بيانات Worker من User
        worker.setName(user.getFullName());
        worker.setCategory(mapRoleToCategory(user.getRole()));
        worker.setCity(user.getGovernorate());
        worker.setArea(user.getCity());
        worker.setPhoneNumber(user.getPhone());
        
        // حالياً: لا يوجد شرط موافقة إدارة -> يعتبر موثق بعد إكمال الملف
        worker.setIsVerified(true);
        worker.setVerificationStatus(com.bena.api.module.worker.entity.VerificationStatus.VERIFIED);

        workerRepository.save(worker);
        log.info("Worker record created/updated for user: {}", user.getId());
    }

    /**
     * التحقق من إمكانية استخدام الميزات الكاملة
     */
    private boolean canUseFullFeatures(User user) {
        // المستخدم العادي والعميل يمكنهم استخدام الميزات مباشرة
        if (user.getRole() == UserRole.USER || user.getRole() == UserRole.CLIENT) {
            return true;
        }

        // التحقق من حالة Worker - إذا كان معلق لا يمكنه الاستخدام
        if (isProfessionalRole(user.getRole())) {
            return workerRepository.findByUserId(user.getId())
                    .map(worker -> worker.getIsActive() && worker.getIsVerified())
                    .orElse(false);
        }

        return false;
    }

    /**
     * حفظ الوثيقة المرفوعة
     */
    private String saveDocument(MultipartFile file, UUID userId) {
        try {
            // إنشاء المجلد إذا لم يكن موجوداً
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // توليد اسم فريد للملف
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") 
                    ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                    : "";
            String filename = userId + "_" + System.currentTimeMillis() + extension;

            // حفظ الملف
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            return "/documents/" + filename;
        } catch (IOException e) {
            log.error("Error saving document", e);
            throw new RuntimeException("Failed to save document", e);
        }
    }

    /**
     * تحديث حالة التحقق من قبل الأدمن
     */
    @Transactional
    public void updateVerificationStatus(UUID userId, VerificationStatus status, String rejectionReason, UUID adminId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setVerificationStatus(status);
        
        if (status == VerificationStatus.APPROVED) {
            user.setDocumentVerified(true);
            user.setVerifiedAt(OffsetDateTime.now());
            user.setVerifiedByAdminId(adminId);
            user.setRejectionReason(null);
        } else if (status == VerificationStatus.REJECTED) {
            user.setDocumentVerified(false);
            user.setRejectionReason(rejectionReason);
        }

        userRepository.save(user);
        log.info("Verification status updated for user: {} to {}", userId, status);

        // تحديث Worker المقابل إذا كان مختصاً
        if (isProfessionalRole(user.getRole())) {
            updateWorkerVerificationStatus(user, status);
        }
    }

    /**
     * تحديث حالة التحقق في Worker عند موافقة/رفض الإدارة
     */
    private void updateWorkerVerificationStatus(User user, VerificationStatus status) {
        workerRepository.findByUserId(user.getId()).ifPresent(worker -> {
            if (status == VerificationStatus.APPROVED) {
                worker.setIsVerified(true);
                worker.setVerificationStatus(com.bena.api.module.worker.entity.VerificationStatus.VERIFIED);
                worker.setVerifiedAt(java.time.LocalDateTime.now());
            } else if (status == VerificationStatus.REJECTED) {
                worker.setIsVerified(false);
                worker.setVerificationStatus(com.bena.api.module.worker.entity.VerificationStatus.REJECTED);
            }
            workerRepository.save(worker);
            log.info("Worker verification status updated for user: {} to {}", user.getId(), status);
        });
    }
}
