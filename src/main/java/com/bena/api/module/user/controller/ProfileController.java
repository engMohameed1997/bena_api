package com.bena.api.module.user.controller;

import com.bena.api.common.dto.ApiResponse;
import com.bena.api.module.user.dto.ProfileCompletionRequest;
import com.bena.api.module.user.dto.ProfileStatusResponse;
import com.bena.api.module.user.dto.UserResponse;
import com.bena.api.module.user.entity.User;
import com.bena.api.module.user.service.ProfileCompletionService;
import com.bena.api.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileCompletionService profileCompletionService;
    private final UserService userService;

    /**
     * الحصول على حالة الملف الشخصي
     */
    @GetMapping("/status")
    public ResponseEntity<ProfileStatusResponse> getProfileStatus(@AuthenticationPrincipal User user) {
        ProfileStatusResponse status = profileCompletionService.getProfileStatus(user.getId());
        return ResponseEntity.ok(status);
    }

    /**
     * إكمال الملف الشخصي برفع الوثيقة
     */
    @PostMapping("/complete")
    public ResponseEntity<Map<String, Object>> completeProfile(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String governorate,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String documentType,
            @RequestParam(required = false) String documentNumber,
            @RequestParam(required = false) MultipartFile document) {

        ProfileCompletionRequest request = new ProfileCompletionRequest();
        request.setGovernorate(governorate);
        request.setCity(city);
        request.setDocumentType(documentType);
        request.setDocumentNumber(documentNumber);

        ProfileStatusResponse status = profileCompletionService.completeProfile(
                user.getId(), request, document);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "تم إكمال الملف الشخصي بنجاح");
        response.put("data", status);

        return ResponseEntity.ok(response);
    }

    /**
     * التحقق من الصلاحيات - يستخدم قبل الوصول للميزات
     */
    @GetMapping("/check-permissions")
    public ResponseEntity<Map<String, Object>> checkPermissions(@AuthenticationPrincipal User user) {
        ProfileStatusResponse status = profileCompletionService.getProfileStatus(user.getId());
        
        Map<String, Object> response = new HashMap<>();
        response.put("canUseFullFeatures", status.getCanUseFullFeatures());
        response.put("profileCompleted", status.getProfileCompleted());
        response.put("verificationStatus", status.getVerificationStatus());
        response.put("rejectionReason", status.getRejectionReason());
        
        return ResponseEntity.ok(response);
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) MultipartFile image) {

        UserResponse response = userService.updateProfile(user.getId(), fullName, phone, city, image);
        return ResponseEntity.ok(ApiResponse.success(response, "تم تحديث الملف الشخصي بنجاح"));
    }
}
