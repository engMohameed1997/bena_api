package com.bena.api.module.project.repository;

import com.bena.api.module.project.entity.Notification;
import com.bena.api.module.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectNotificationRepository extends JpaRepository<Notification, UUID> {

    Page<Notification> findByUser(User user, Pageable pageable);

    Page<Notification> findByUserAndIsRead(User user, Boolean isRead, Pageable pageable);

    List<Notification> findByUserAndNotificationType(User user, Notification.NotificationType notificationType);

    Long countByUserAndIsRead(User user, Boolean isRead);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP WHERE n.user = :user AND n.isRead = false")
    void markAllAsReadByUser(@Param("user") User user);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP WHERE n.id = :id")
    void markAsRead(@Param("id") UUID id);

    void deleteByUserAndIsRead(User user, Boolean isRead);
}
