package com.bena.api.module.admin.controller;

import com.bena.api.common.dto.ApiResponse;
import com.bena.api.module.user.dto.UserResponse;
import com.bena.api.module.user.entity.User;
import com.bena.api.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller لجلب معلومات الأدمن الحالي
 */
@RestController
@RequestMapping("/v1/admin/profile")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProfileController {

    private final UserRepository userRepository;

    /**
     * جلب معلومات الأدمن الحالي (اسم فقط للأمان)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<AdminProfileResponse>> getCurrentAdminProfile(Authentication authentication) {
        String email = authentication.getName();
        
        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("الأدمن غير موجود"));

        AdminProfileResponse response = AdminProfileResponse.builder()
                .fullName(admin.getFullName())
                .role("ADMIN") // دائماً ADMIN لأن الـ endpoint محمي
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}

/**
 * Response يحتوي فقط على الاسم (بدون email للأمان)
 */
@lombok.Data
@lombok.Builder
class AdminProfileResponse {
    private String fullName;
    private String role;
}
