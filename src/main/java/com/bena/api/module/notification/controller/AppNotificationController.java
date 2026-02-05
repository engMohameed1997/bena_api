package com.bena.api.module.notification.controller;

import com.bena.api.common.dto.ApiResponse;
import com.bena.api.module.auth.service.JwtService;
import com.bena.api.module.notification.dto.SendNotificationRequest;
import com.bena.api.module.notification.entity.AppNotification;
import com.bena.api.module.notification.service.AppNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "نظام الإشعارات")
public class AppNotificationController {

    private final AppNotificationService notificationService;
    private final JwtService jwtService;

    // ==================== User Endpoints ====================

    @GetMapping("/notifications")
    @Operation(summary = "إشعاراتي", description = "جلب الإشعارات الخاصة بالمستخدم الحالي")
    public ResponseEntity<ApiResponse<Page<AppNotification>>> getMyNotifications(
            @RequestHeader("Authorization") String authHeader,
            @PageableDefault(sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        
        String token = authHeader.replace("Bearer ", "");
        UUID userId = UUID.fromString(jwtService.extractUserId(token));
        
        return ResponseEntity.ok(ApiResponse.success(
            notificationService.getUserNotifications(userId, pageable)
        ));
    }

    @GetMapping("/notifications/unread-count")
    @Operation(summary = "عدد غير المقروء", description = "جلب عدد الإشعارات غير المقروءة")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            @RequestHeader("Authorization") String authHeader) {
        
        String token = authHeader.replace("Bearer ", "");
        UUID userId = UUID.fromString(jwtService.extractUserId(token));
        
        return ResponseEntity.ok(ApiResponse.success(
            notificationService.getUnreadCount(userId)
        ));
    }

    @PutMapping("/notifications/{id}/read")
    @Operation(summary = "تحديد كمقروء", description = "تحديد إشعار معين كمقروء")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/notifications/read-all")
    @Operation(summary = "تحديد الكل كمقروء", description = "تحديد جميع إشعارات المستخدم كمقروءة")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @RequestHeader("Authorization") String authHeader) {
        
        String token = authHeader.replace("Bearer ", "");
        UUID userId = UUID.fromString(jwtService.extractUserId(token));
        
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // ==================== Admin Endpoints ====================

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/notifications/send")
    @Operation(summary = "إرسال إشعار (Admin)", description = "إرسال إشعار لمستخدم معين أو للجميع")
    public ResponseEntity<ApiResponse<Void>> sendNotification(
            @Valid @RequestBody SendNotificationRequest request) {
        
        if (request.getUserId() != null) {
            notificationService.sendToUser(request.getUserId(), request.getTitle(), request.getMessage(), request.getType());
        } else {
            notificationService.sendToAll(request.getTitle(), request.getMessage(), request.getType());
        }
        
        return ResponseEntity.ok(ApiResponse.success(null, "تم إرسال الإشعار بنجاح"));
    }
}
