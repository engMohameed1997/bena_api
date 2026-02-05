package com.bena.api.module.project.controller;

import com.bena.api.module.project.dto.NotificationResponse;
import com.bena.api.module.project.service.ProjectNotificationService;
import com.bena.api.module.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/project-notifications")
@RequiredArgsConstructor
public class ProjectNotificationController {

    private final ProjectNotificationService notificationService;

    @GetMapping
    public ResponseEntity<?> getMyNotifications(
            @AuthenticationPrincipal User user,
            Pageable pageable) {
        Page<NotificationResponse> notifications = notificationService.getUserNotifications(user, pageable);
        return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "data", notifications
        ));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCount(@AuthenticationPrincipal User user) {
        Long count = notificationService.getUnreadCount(user);
        return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "count", count != null ? count : 0
        ));
    }

    @PostMapping("/{notificationId}/read")
    public ResponseEntity<?> markAsRead(@PathVariable UUID notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "تم تحديد الإشعار كمقروء"
        ));
    }

    @PostMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(@AuthenticationPrincipal User user) {
        notificationService.markAllAsRead(user);
        return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "تم تحديد جميع الإشعارات كمقروءة"
        ));
    }
}
