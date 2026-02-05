package com.bena.api.module.user.controller;

import com.bena.api.common.dto.ApiResponse;
import com.bena.api.common.dto.PageResponse;
import com.bena.api.module.user.dto.ChangePasswordRequest;
import com.bena.api.module.user.dto.UserRequest;
import com.bena.api.module.user.dto.UserResponse;
import com.bena.api.module.user.dto.UserUpdateRequest;
import com.bena.api.module.user.entity.User;
import com.bena.api.module.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * إدارة المستخدمين - CRUD
 * User Management - CRUD Operations
 */
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "إدارة المستخدمين")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "جلب جميع المستخدمين", description = "يعرض قائمة المستخدمين مع دعم الترقيم")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        
        Page<UserResponse> users = userService.getAllUsers(pageRequest);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(users)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "جلب مستخدم بالمعرف", description = "يعرض تفاصيل مستخدم محدد")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PostMapping
    @Operation(summary = "إنشاء مستخدم جديد", description = "يضيف مستخدم جديد للنظام")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody UserRequest request) {
        
        UserResponse user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(user, "تم إنشاء المستخدم بنجاح"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "تحديث مستخدم", description = "يحدث بيانات مستخدم موجود")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateRequest request) {
        
        UserResponse user = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success(user, "تم تحديث المستخدم بنجاح"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "حذف مستخدم", description = "يحذف مستخدم (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(null, "تم حذف المستخدم بنجاح"));
    }

    @PutMapping("/change-password")
    @Operation(summary = "تغيير كلمة المرور", description = "تغيير كلمة المرور للمستخدم الحالي")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ChangePasswordRequest request) {

        userService.changePassword(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(null, "تم تغيير كلمة المرور بنجاح"));
    }
}
