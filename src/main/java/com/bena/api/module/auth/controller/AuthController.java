package com.bena.api.module.auth.controller;

import com.bena.api.common.dto.ApiResponse;
import com.bena.api.module.auth.dto.*;
import com.bena.api.module.auth.service.AuthService;
import com.bena.api.module.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * المصادقة - تسجيل الدخول والتسجيل
 * Authentication - Login and Registration
 */
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "المصادقة - تسجيل الدخول والتسجيل")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "تسجيل مستخدم جديد", description = "إنشاء حساب جديد في النظام")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "تم إنشاء الحساب بنجاح"));
    }

    @PostMapping("/login")
    @Operation(summary = "تسجيل الدخول", description = "تسجيل الدخول باستخدام البريد الإلكتروني وكلمة المرور")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "تم تسجيل الدخول بنجاح"));
    }

    @GetMapping("/me")
    @Operation(summary = "المستخدم الحالي", description = "جلب بيانات المستخدم الحالي من التوكن")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @RequestHeader("Authorization") String authHeader) {
        
        String token = authHeader.replace("Bearer ", "");
        UserResponse user = authService.getCurrentUser(token);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @PostMapping("/forgot-password")
    @Operation(summary = "نسيت كلمة المرور", description = "إرسال رمز إعادة تعيين كلمة المرور")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        
        authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success(null, "تم إرسال رمز التحقق إلى بريدك الإلكتروني"));
    }
    
    @PostMapping("/reset-password")
    @Operation(summary = "إعادة تعيين كلمة المرور", description = "تعيين كلمة مرور جديدة باستخدام رمز التحقق")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success(null, "تم تغيير كلمة المرور بنجاح"));
    }
    
    @GetMapping("/verify-reset-token")
    @Operation(summary = "التحقق من رمز إعادة التعيين", description = "التحقق من صلاحية رمز إعادة تعيين كلمة المرور")
    public ResponseEntity<ApiResponse<Boolean>> verifyResetToken(
            @RequestParam String token) {
        
        boolean isValid = authService.verifyResetToken(token);
        return ResponseEntity.ok(ApiResponse.success(isValid));
    }
    
    @PostMapping("/verify-email")
    @Operation(summary = "تأكيد البريد الإلكتروني", description = "تأكيد البريد الإلكتروني باستخدام رمز التحقق")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(
            @Valid @RequestBody VerifyEmailRequest request) {
        
        authService.verifyEmail(request);
        return ResponseEntity.ok(ApiResponse.success(null, "تم تأكيد البريد الإلكتروني بنجاح"));
    }
    
    @PostMapping("/resend-verification")
    @Operation(summary = "إعادة إرسال رمز التأكيد", description = "إعادة إرسال رمز تأكيد البريد الإلكتروني")
    public ResponseEntity<ApiResponse<Void>> resendVerification(
            @RequestParam String email) {
        
        authService.resendVerificationEmail(email);
        return ResponseEntity.ok(ApiResponse.success(null, "تم إرسال رمز التحقق"));
    }
}
