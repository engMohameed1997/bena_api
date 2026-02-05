package com.bena.api.module.user.entity;

import com.bena.api.module.user.enums.UserRole;
import com.bena.api.module.user.enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(name = "password_hash")
    private String passwordHash;

    // Social Authentication IDs
    @Column(name = "google_id", unique = true)
    private String googleId;

    @Column(name = "apple_id", unique = true)
    private String appleId;

    @Column(name = "profile_picture", length = 500)
    private String profilePicture;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    @Builder.Default
    private UserRole role = UserRole.USER;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "email_verified")
    @Builder.Default
    private Boolean emailVerified = false;

    // المحافظة والموقع
    @Column(length = 50)
    private String governorate;

    @Column(length = 100)
    private String city;

    @Column(name = "profile_picture_url", length = 500)
    private String profilePictureUrl;

    // حقول اكتمال الملف الشخصي
    @Column(name = "profile_completed")
    @Builder.Default
    private Boolean profileCompleted = false;

    @Column(name = "document_verified")
    @Builder.Default
    private Boolean documentVerified = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", length = 20)
    @Builder.Default
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    // حقول الوثائق
    @Column(name = "document_type", length = 50)
    private String documentType;

    @Column(name = "document_url", length = 500)
    private String documentUrl;

    @Column(name = "document_number", length = 100)
    private String documentNumber;

    // حقول التحقق
    @Column(name = "verified_at")
    private OffsetDateTime verifiedAt;

    @Column(name = "verified_by_admin_id")
    private UUID verifiedByAdminId;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Column(name = "failed_login_attempts")
    @Builder.Default
    private int failedLoginAttempts = 0;

    @Column(name = "lock_time")
    private OffsetDateTime lockTime;

    @Override
    public boolean isAccountNonLocked() {
        if (lockTime != null && lockTime.isAfter(OffsetDateTime.now())) {
            return false;
        }
        return Boolean.TRUE.equals(isActive);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(isActive) && Boolean.TRUE.equals(emailVerified);
    }
}
