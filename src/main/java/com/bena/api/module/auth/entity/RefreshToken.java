package com.bena.api.module.auth.entity;

import com.bena.api.module.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * كيان Refresh Token
 * يُستخدم لتجديد الـ Access Token بدون إعادة تسجيل الدخول
 */
@Entity
@Table(name = "refresh_tokens", indexes = {
    @Index(name = "idx_refresh_token", columnList = "token"),
    @Index(name = "idx_refresh_user_id", columnList = "user_id"),
    @Index(name = "idx_refresh_expires_at", columnList = "expires_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "device_info", length = 500)
    private String deviceInfo;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;

    @Column(name = "is_revoked")
    @Builder.Default
    private Boolean isRevoked = false;

    @Column(name = "revoked_at")
    private OffsetDateTime revokedAt;

    @Column(name = "revoked_reason", length = 100)
    private String revokedReason;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "last_used_at")
    private OffsetDateTime lastUsedAt;

    /**
     * التحقق من صلاحية التوكن
     */
    public boolean isValid() {
        return !Boolean.TRUE.equals(isRevoked) && 
               expiresAt != null && 
               expiresAt.isAfter(OffsetDateTime.now());
    }

    /**
     * إلغاء التوكن
     */
    public void revoke(String reason) {
        this.isRevoked = true;
        this.revokedAt = OffsetDateTime.now();
        this.revokedReason = reason;
    }
}
