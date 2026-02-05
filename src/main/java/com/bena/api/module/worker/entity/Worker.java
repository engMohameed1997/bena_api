package com.bena.api.module.worker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * كيان العامل/الخلفة
 */
@Entity
@Table(name = "workers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ربط Worker بـ User (المستخدم المسجل)
    @Column(name = "user_id")
    private UUID userId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkerCategory category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "whatsapp_number", length = 20)
    private String whatsappNumber;

    // صورة العامل الشخصية - URL (للصور الجديدة)
    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;
    
    // الأعمدة القديمة - للتوافق مع البيانات الموجودة
    @Column(name = "profile_image")
    private byte[] profileImage;

    @Column(name = "profile_image_type", length = 50)
    private String profileImageType;

    // متوسط التقييم
    @Column(name = "average_rating")
    @Builder.Default
    private Double averageRating = 0.0;

    // عدد التقييمات
    @Column(name = "review_count")
    @Builder.Default
    private Integer reviewCount = 0;

    // هل مميز (نجمة)
    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;

    // هل نشط
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    // سنوات الخبرة
    @Column(name = "experience_years")
    private Integer experienceYears;

    // الموقع/المنطقة (قديم - للتوافق)
    @Column(length = 100)
    private String location;

    // ========== الموقع الجغرافي الجديد ==========
    @Column(length = 100)
    private String city; // المحافظة

    @Column(length = 100)
    private String area; // القضاء/المنطقة

    private Double latitude; // خط العرض

    private Double longitude; // خط الطول

    // ========== الأسعار ==========
    @Column(name = "price_per_meter", precision = 10, scale = 2)
    private java.math.BigDecimal pricePerMeter; // سعر المتر

    @Column(name = "price_per_day", precision = 10, scale = 2)
    private java.math.BigDecimal pricePerDay; // سعر اليومية

    @Column(name = "price_per_visit", precision = 10, scale = 2)
    private java.math.BigDecimal pricePerVisit; // سعر الزيارة

    // ========== معلومات إضافية ==========
    @Column(name = "works_at_night")
    @Builder.Default
    private Boolean worksAtNight = false; // يعمل بالليل

    @Column(name = "estimated_completion_days")
    private Integer estimatedCompletionDays; // مدة إنجاز العمل التقديرية

    // ========== حقول التوثيق ==========
    @Column(name = "is_verified")
    @Builder.Default
    private Boolean isVerified = false;

    @Column(name = "verification_status", length = 50)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    @Column(name = "id_card_number", length = 50)
    private String idCardNumber;

    @Column(name = "id_card_image_url", length = 500)
    private String idCardImageUrl;

    @Column(name = "license_number", length = 50)
    private String licenseNumber;

    @Column(name = "license_image_url", length = 500)
    private String licenseImageUrl;

    @Column(name = "certificate_urls", columnDefinition = "TEXT")
    private String certificateUrls;

    @Column(name = "verification_notes", columnDefinition = "TEXT")
    private String verificationNotes;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "verified_by_admin_id")
    private UUID verifiedByAdminId;

    // ========== حقول المهندسين ==========
    @Column(name = "syndicate_id", length = 50)
    private String syndicateId; // رقم هوية النقابة

    @Column(name = "syndicate_card_url", length = 500)
    private String syndicateCardUrl; // صورة هوية النقابة

    @Column(length = 100)
    private String degree; // الشهادة (بكالوريوس، ماجستير، دكتوراه)

    @Column(name = "degree_certificate_url", length = 500)
    private String degreeCertificateUrl; // صورة الشهادة

    @Column(length = 100)
    private String specialization; // التخصص (معماري، مدني، كهرباء)

    @Column(name = "completed_projects_count")
    @Builder.Default
    private Integer completedProjectsCount = 0; // عدد المشاريع المنجزة

    // ========== حقول المقاولين ==========
    @Column(name = "contractor_license", length = 50)
    private String contractorLicense; // رقم رخصة المقاولة

    @Column(name = "contractor_license_url", length = 500)
    private String contractorLicenseUrl; // صورة رخصة المقاولة

    @Column(name = "license_type", length = 10)
    private String licenseType; // نوع الرخصة (A, B, C, D)

    @Column(name = "license_expiry_date")
    private java.time.LocalDate licenseExpiryDate; // تاريخ انتهاء الرخصة

    @Column(precision = 15, scale = 2)
    private java.math.BigDecimal capital; // رأس المال

    @Column(name = "employees_count")
    private Integer employeesCount; // عدد العمال

    @Column(name = "current_projects_count")
    @Builder.Default
    private Integer currentProjectsCount = 0; // عدد المشاريع الحالية

    // ========== حقول المصممين ==========
    @Column(name = "design_type", length = 50)
    private String designType; // نوع التصميم (interior, exterior, landscape)

    @Column(name = "software_skills", columnDefinition = "TEXT")
    private String softwareSkills; // البرامج المستخدمة

    @Column(name = "portfolio_url", length = 500)
    private String portfolioUrl; // رابط معرض الأعمال

    @Column(name = "design_style", length = 50)
    private String designStyle; // الأسلوب (modern, classic, minimalist)

    // ========== حقول عامة إضافية ==========
    @Column(name = "specialized_experience_years")
    private Integer specializedExperienceYears; // سنوات الخبرة المتخصصة

    @Column(columnDefinition = "TEXT")
    private String awards; // الجوائز والشهادات

    @Column(length = 255)
    private String languages; // اللغات

    @Column(name = "working_hours", length = 100)
    private String workingHours; // ساعات العمل

    @Column(name = "minimum_project_budget", precision = 15, scale = 2)
    private java.math.BigDecimal minimumProjectBudget; // الحد الأدنى للمشروع

    // ========== بيانات تسجيل الدخول ==========
    @Column(length = 255)
    private String email;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    // معرض أعمال العامل
    @OneToMany(mappedBy = "worker", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<WorkerMedia> mediaGallery = new ArrayList<>();

    // تقييمات العامل
    @OneToMany(mappedBy = "worker", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<WorkerReview> reviews = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // دالة لتحديث متوسط التقييم
    public void updateAverageRating() {
        if (reviews == null || reviews.isEmpty()) {
            this.averageRating = 0.0;
            this.reviewCount = 0;
        } else {
            this.averageRating = reviews.stream()
                    .mapToInt(WorkerReview::getRating)
                    .average()
                    .orElse(0.0);
            this.reviewCount = reviews.size();
        }
    }
}
