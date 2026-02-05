package com.bena.api.module.fcm.entity;

import com.bena.api.module.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * 📱 UserFcmToken Entity
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * Purpose: Store multiple FCM tokens per user
 * Relation: Many-to-One with User
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 */
@Entity
@Table(name = "user_fcm_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFcmToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "fcm_token", nullable = false, unique = true, columnDefinition = "TEXT")
    private String fcmToken;

    @Column(name = "device_type", length = 50)
    private String deviceType;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "last_used_at")
    private OffsetDateTime lastUsedAt;
}
