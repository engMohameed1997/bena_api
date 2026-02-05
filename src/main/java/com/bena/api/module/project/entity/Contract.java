package com.bena.api.module.project.entity;

import com.bena.api.module.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * كيان العقد الرقمي - عقد بين العميل والمختص
 */
@Entity
@Table(name = "contracts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false, unique = true)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private User provider;

    @Column(name = "contract_terms", columnDefinition = "TEXT", nullable = false)
    private String contractTerms;

    @Column(name = "payment_terms", columnDefinition = "TEXT")
    private String paymentTerms;

    @Column(name = "delivery_terms", columnDefinition = "TEXT")
    private String deliveryTerms;

    @Column(name = "cancellation_policy", columnDefinition = "TEXT")
    private String cancellationPolicy;

    @Column(name = "client_signed")
    @Builder.Default
    private Boolean clientSigned = false;

    @Column(name = "client_signed_at")
    private LocalDateTime clientSignedAt;

    @Column(name = "client_ip_address", length = 50)
    private String clientIpAddress;

    @Column(name = "provider_signed")
    @Builder.Default
    private Boolean providerSigned = false;

    @Column(name = "provider_signed_at")
    private LocalDateTime providerSignedAt;

    @Column(name = "provider_ip_address", length = 50)
    private String providerIpAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private ContractStatus status = ContractStatus.DRAFT;

    @Column(name = "contract_start_date")
    private LocalDateTime contractStartDate;

    @Column(name = "contract_end_date")
    private LocalDateTime contractEndDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum ContractStatus {
        DRAFT,            // مسودة
        PENDING_SIGNATURE,// في انتظار التوقيع
        ACTIVE,           // نشط
        COMPLETED,        // مكتمل
        TERMINATED,       // منتهي
        CANCELLED         // ملغي
    }

    public boolean isFullySigned() {
        return Boolean.TRUE.equals(clientSigned) && Boolean.TRUE.equals(providerSigned);
    }
}
