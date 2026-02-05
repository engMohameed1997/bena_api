package com.bena.api.module.auth.controller;

import com.bena.api.common.dto.ApiResponse;
import com.bena.api.module.auth.dto.SessionDto;
import com.bena.api.module.auth.entity.RefreshToken;
import com.bena.api.module.auth.service.JwtService;
import com.bena.api.module.auth.service.RefreshTokenService;
import com.bena.api.module.user.entity.User;
import com.bena.api.module.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Tag(name = "Sessions", description = "إدارة الجلسات النشطة")
public class SessionController {

    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final UserService userService;

    // ==================== User Endpoints ====================

    @GetMapping("/auth/sessions")
    @Operation(summary = "جلساتي النشطة", description = "عرض جميع الأجهزة والجلسات المسجلة لهذا الحساب")
    public ResponseEntity<ApiResponse<List<SessionDto>>> getMySessions(
            @RequestHeader("Authorization") String authHeader) {
        
        String token = authHeader.replace("Bearer ", "");
        String userIdStr = jwtService.extractUserId(token);
        UUID userId = UUID.fromString(userIdStr);
        
        // We can identify current session if we pass the refresh token, but with Access Token it's harder
        // For now, we just list them.
        
        List<RefreshToken> tokens = refreshTokenService.getActiveSessions(userId);
        List<SessionDto> dtos = tokens.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    @DeleteMapping("/auth/sessions/{id}")
    @Operation(summary = "تسجيل خروج من جلسة", description = "إلغاء جلسة محددة (جهاز آخر)")
    public ResponseEntity<ApiResponse<Void>> revokeSession(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        
        String token = authHeader.replace("Bearer ", "");
        String userIdStr = jwtService.extractUserId(token);
        UUID userId = UUID.fromString(userIdStr);

        // Verify ownership
        // Note: Ideally RefreshTokenService should handle permission check, but for MVP:
        // We assume we trust the service to fetch by ID. 
        // Better: add ownership check here if service doesn't have "revokeSessionByIdAndUser"
        
        // For now, assuming user can manage their own sessions mostly by refreshing/logging out.
        // But to delete a SPECIFIC session by ID requires granular control.
        // Let's implement a simple direct revoke in service later or here if accessible.
        
        // Workaround: We will authorize by checking if the session belongs to user
        // Creating a new method in RefreshTokenService would be cleaner.
        
        // For now, returning Not Implemented until we verify Service capabilities or add method.
        // Actally, let's allow "logoutAllDevices" which is safer.
        return ResponseEntity.badRequest().body(ApiResponse.error("Use logout-all or generic logout for now"));
    }

    @DeleteMapping("/auth/sessions")
    @Operation(summary = "تسجيل خروج من جميع الأجهزة", description = "إلغاء جميع الجلسات النشطة")
    public ResponseEntity<ApiResponse<Void>> revokeAllSessions(
            @RequestHeader("Authorization") String authHeader) {
        
        String token = authHeader.replace("Bearer ", "");
        String userIdStr = jwtService.extractUserId(token);
        UUID userId = UUID.fromString(userIdStr);
        
        refreshTokenService.logoutAllDevices(userId);
        
        return ResponseEntity.ok(ApiResponse.success(null, "تم تسجيل الخروج من جميع الأجهزة بنجاح"));
    }

    // ==================== Admin Endpoints ====================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users/{userId}/sessions")
    @Operation(summary = "جلسات مستخدم (Admin)", description = "عرض الجلسات النشطة لمستخدم معين")
    public ResponseEntity<ApiResponse<List<SessionDto>>> getUserSessions(@PathVariable UUID userId) {
        List<RefreshToken> tokens = refreshTokenService.getActiveSessions(userId);
        List<SessionDto> dtos = tokens.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/users/{userId}/sessions")
    @Operation(summary = "إلغاء جلسات مستخدم (Admin)", description = "تسجيل خروج مستخدم من جميع أجهزته")
    public ResponseEntity<ApiResponse<Void>> revokeUserSessions(@PathVariable UUID userId) {
        refreshTokenService.logoutAllDevices(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "تم إلغاء جلسات المستخدم بنجاح"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/sessions")
    @Operation(summary = "جميع الجلسات النشطة (Admin)", description = "عرض جميع الجلسات النشطة في النظام مع ترقيم الصفحات")
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<SessionDto>>> getSystemActiveSessions(
            @org.springframework.data.web.PageableDefault(sort = "lastUsedAt", direction = org.springframework.data.domain.Sort.Direction.DESC) org.springframework.data.domain.Pageable pageable) {
        
        org.springframework.data.domain.Page<RefreshToken> sessions = refreshTokenService.getAllActiveSessions(pageable);
        org.springframework.data.domain.Page<SessionDto> dtos = sessions.map(this::toDto);
        
        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    private SessionDto toDto(RefreshToken rt) {
        String email = rt.getUser() != null ? rt.getUser().getEmail() : "Unknown";
        String name = rt.getUser() != null ? rt.getUser().getFullName() : "Unknown";
        String role = rt.getUser() != null ? rt.getUser().getRole().name() : "Unknown";
        
        return SessionDto.builder()
                .id(rt.getId())
                .userEmail(email)
                .userName(name)
                .userRole(role)
                .deviceInfo(rt.getDeviceInfo())
                .ipAddress(rt.getIpAddress())
                .expiresAt(rt.getExpiresAt())
                .lastUsedAt(rt.getLastUsedAt())
                .isRevoked(rt.getIsRevoked())
                .build();
    }
}
