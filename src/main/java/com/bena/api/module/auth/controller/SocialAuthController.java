package com.bena.api.module.auth.controller;

import com.bena.api.common.dto.ApiResponse;
import com.bena.api.module.auth.dto.AppleAuthRequest;
import com.bena.api.module.auth.dto.AuthResponse;
import com.bena.api.module.auth.dto.GoogleAuthRequest;
import com.bena.api.module.auth.service.SocialAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * المصادقة الاجتماعية - Google و Apple
 * Social Authentication - Google and Apple Sign-In
 */
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Social Authentication", description = "المصادقة الاجتماعية - Google و Apple")
public class SocialAuthController {

    private final SocialAuthService socialAuthService;

    @PostMapping("/google")
    @Operation(summary = "تسجيل الدخول بـ Google", description = "تسجيل الدخول أو إنشاء حساب باستخدام Google")
    public ResponseEntity<ApiResponse<AuthResponse>> googleAuth(
            @Valid @RequestBody GoogleAuthRequest request) {

        AuthResponse response = socialAuthService.authenticateWithGoogle(request);
        return ResponseEntity.ok(ApiResponse.success(response, "تم تسجيل الدخول بـ Google بنجاح"));
    }

    @PostMapping("/apple")
    @Operation(summary = "تسجيل الدخول بـ Apple", description = "تسجيل الدخول أو إنشاء حساب باستخدام Apple")
    public ResponseEntity<ApiResponse<AuthResponse>> appleAuth(
            @Valid @RequestBody AppleAuthRequest request) {

        AuthResponse response = socialAuthService.authenticateWithApple(request);
        return ResponseEntity.ok(ApiResponse.success(response, "تم تسجيل الدخول بـ Apple بنجاح"));
    }
}
