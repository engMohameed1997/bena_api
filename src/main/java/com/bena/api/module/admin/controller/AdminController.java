package com.bena.api.module.admin.controller;

import com.bena.api.common.dto.ApiResponse;
import com.bena.api.module.buildingsteps.service.BuildingStepsService;
import com.bena.api.module.user.repository.UserRepository;
import com.bena.api.module.worker.repository.WorkerRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin Controller - إحصائيات عامة
 * (تم نقل إدارة المحتوى إلى BuildingStepAdminController لمنع التكرار)
 */
@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "لوحة التحكم الرئيسية")
public class AdminController {
    
    private final BuildingStepsService buildingStepsService;
    private final UserRepository userRepository;
    private final WorkerRepository workerRepository;
    
    // ==================== إحصائيات ====================
    
    @GetMapping("/stats")
    @Operation(summary = "إحصائيات عامة للنظام")
    public ResponseEntity<ApiResponse<AdminStats>> getStats() {
        AdminStats stats = new AdminStats();
        stats.setTotalCategories(buildingStepsService.getAllCategories().size());
        stats.setTotalSteps(buildingStepsService.getAllSteps().size());
        stats.setTotalUsers((int) userRepository.count());
        stats.setTotalWorkers((int) workerRepository.count());
        // stats.setTotalMedia(...); // تحتاج طريقة لجلب العدد
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
    
    // DTO للإحصائيات
    public static class AdminStats {
        private int totalCategories;
        private int totalSteps;
        private int totalUsers;
        private int totalWorkers;
        
        public int getTotalCategories() { return totalCategories; }
        public void setTotalCategories(int totalCategories) { this.totalCategories = totalCategories; }
        
        public int getTotalSteps() { return totalSteps; }
        public void setTotalSteps(int totalSteps) { this.totalSteps = totalSteps; }
        
        public int getTotalUsers() { return totalUsers; }
        public void setTotalUsers(int totalUsers) { this.totalUsers = totalUsers; }
        
        public int getTotalWorkers() { return totalWorkers; }
        public void setTotalWorkers(int totalWorkers) { this.totalWorkers = totalWorkers; }
    }
}

