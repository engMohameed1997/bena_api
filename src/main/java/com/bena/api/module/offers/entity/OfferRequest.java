package com.bena.api.module.offers.entity;

import com.bena.api.module.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * كيان طلب العرض
 */
@Entity
@Table(name = "offer_requests")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id", nullable = false)
    private ContractorOffer offer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(length = 20)
    private String phone;

    @Column(name = "project_area")
    private Integer projectArea;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    @Builder.Default
    private OfferRequestStatus status = OfferRequestStatus.PENDING;

    @Column(name = "provider_notes", columnDefinition = "TEXT")
    private String providerNotes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
