package com.bena.api.module.notification.entity;

import com.bena.api.module.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "app_notifications", indexes = {
    @Index(name = "idx_app_notification_user_id", columnList = "user_id"),
    @Index(name = "idx_app_notification_created_at", columnList = "created_at"),
    @Index(name = "idx_app_notification_read", columnList = "is_read")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "user_id")
    private UUID userId; // Can be null for system-wide notifications

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private NotificationType type = NotificationType.INFO;

    @Column(name = "is_read")
    @Builder.Default
    private boolean isRead = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    public enum NotificationType {
        INFO, SUCCESS, WARNING, ERROR
    }
}
