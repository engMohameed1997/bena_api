package com.bena.api.module.buildingsteps.controller;

import com.bena.api.module.buildingsteps.dto.*;
import com.bena.api.module.buildingsteps.service.BuildingStepsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/building-steps")
public class BuildingStepsController {
    
    @Autowired
    private BuildingStepsService buildingStepsService;
    
    // ==================== Categories ====================
    
    @GetMapping("/categories")
    public ResponseEntity<Map<String, Object>> getAllCategories() {
        List<StepCategoryDto> categories = buildingStepsService.getAllCategories();
        return ResponseEntity.ok(successResponse(categories));
    }
    
    @GetMapping("/categories/{id}")
    public ResponseEntity<Map<String, Object>> getCategoryById(@PathVariable Long id) {
        StepCategoryDto category = buildingStepsService.getCategoryById(id);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(successResponse(category));
    }
    
    // ==================== Building Steps ====================
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllSteps() {
        List<BuildingStepDto> steps = buildingStepsService.getAllSteps();
        return ResponseEntity.ok(successResponse(steps));
    }
    
    @GetMapping("/by-category/{categoryId}")
    public ResponseEntity<Map<String, Object>> getStepsByCategory(@PathVariable Long categoryId) {
        List<BuildingStepDto> steps = buildingStepsService.getStepsByCategory(categoryId);
        return ResponseEntity.ok(successResponse(steps));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getStepById(@PathVariable Long id) {
        BuildingStepDto step = buildingStepsService.getStepById(id);
        if (step == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(successResponse(step));
    }
    
    @GetMapping("/{id}/details")
    public ResponseEntity<Map<String, Object>> getStepWithDetails(@PathVariable Long id) {
        BuildingStepDto step = buildingStepsService.getStepWithDetails(id);
        if (step == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(successResponse(step));
    }
    
    // Note: Admin operations (Create, Update, Delete) are now handled in BuildingStepAdminController
    // under /v1/admin/building-steps using standardized ApiResponse.
    
    // ==================== Helper Methods ====================
    
    private Map<String, Object> successResponse(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        return response;
    }
    
    private Map<String, Object> successResponse(Object data, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        return response;
    }
    
    private Map<String, Object> errorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
}
