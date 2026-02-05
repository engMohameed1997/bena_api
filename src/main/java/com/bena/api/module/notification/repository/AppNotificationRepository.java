package com.bena.api.module.notification.repository;

import com.bena.api.module.notification.entity.AppNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AppNotificationRepository extends JpaRepository<AppNotification, Long> {
    
    Page<AppNotification> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    
    // Find unread notifications count
    long countByUserIdAndIsReadFalse(UUID userId);
    
    @Modifying
    @Query("UPDATE AppNotification n SET n.isRead = true WHERE n.userId = :userId")
    void markAllAsRead(UUID userId);
}
