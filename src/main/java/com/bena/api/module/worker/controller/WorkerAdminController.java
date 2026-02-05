package com.bena.api.module.worker.controller;

import com.bena.api.module.worker.dto.*;
import com.bena.api.module.worker.entity.WorkerCategory;
import com.bena.api.module.worker.service.WorkerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Controller للأدمن - إدارة العمال
 * ✅ محمي بـ @PreAuthorize
 */
@RestController
@RequestMapping("/v1/admin/workers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Workers Admin", description = "إدارة العمال - للأدمن فقط")
public class WorkerAdminController {

    private final WorkerService workerService;

    @GetMapping
    @Operation(summary = "جلب جميع العمال للأدمن (بما فيهم المعلقين)")
    public ResponseEntity<com.bena.api.common.dto.ApiResponse<Page<WorkerDTO>>> getAllWorkers(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<WorkerDTO> workers = workerService.getAllWorkersForAdmin(pageable);
        return ResponseEntity.ok(com.bena.api.common.dto.ApiResponse.success(workers));
    }

    @PostMapping
    @Operation(summary = "إضافة عامل جديد")
    public ResponseEntity<WorkerDTO> createWorker(
            @RequestParam String name,
            @RequestParam WorkerCategory category,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String whatsappNumber,
            @RequestParam(required = false) Integer experienceYears,
            @RequestParam(required = false) String location,
            @RequestParam(required = false, defaultValue = "false") Boolean isFeatured,
            @RequestParam(required = false) MultipartFile profileImage
    ) throws IOException {
        WorkerCreateDTO dto = WorkerCreateDTO.builder()
                .name(name)
                .category(category)
                .description(description)
                .phoneNumber(phoneNumber)
                .whatsappNumber(whatsappNumber)
                .experienceYears(experienceYears)
                .location(location)
                .isFeatured(isFeatured)
                .build();

        return ResponseEntity.ok(workerService.createWorker(dto, profileImage));
    }

    @PutMapping("/{id}")
    @Operation(summary = "تحديث عامل")
    public ResponseEntity<WorkerDTO> updateWorker(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam WorkerCategory category,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String whatsappNumber,
            @RequestParam(required = false) Integer experienceYears,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Boolean isFeatured,
            @RequestParam(required = false) MultipartFile profileImage
    ) throws IOException {
        WorkerCreateDTO dto = WorkerCreateDTO.builder()
                .name(name)
                .category(category)
                .description(description)
                .phoneNumber(phoneNumber)
                .whatsappNumber(whatsappNumber)
                .experienceYears(experienceYears)
                .location(location)
                .isFeatured(isFeatured)
                .build();

        return ResponseEntity.ok(workerService.updateWorker(id, dto, profileImage));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "حذف عامل")
    public ResponseEntity<Void> deleteWorker(@PathVariable Long id) {
        workerService.deleteWorker(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/toggle-status")
    @Operation(summary = "تفعيل/تعليق حساب عامل")
    public ResponseEntity<com.bena.api.common.dto.ApiResponse<WorkerDTO>> toggleStatus(@PathVariable Long id) {
        WorkerDTO worker = workerService.toggleWorkerActive(id);
        return ResponseEntity.ok(com.bena.api.common.dto.ApiResponse.success(worker, 
            Boolean.TRUE.equals(worker.getIsActive()) ? "تم تفعيل الحساب بنجاح" : "تم تعليق الحساب بنجاح"));
    }

    @PatchMapping("/{id}/toggle-active")
    @Operation(summary = "تفعيل/إلغاء تفعيل عامل")
    public ResponseEntity<WorkerDTO> toggleActive(@PathVariable Long id) {
        return ResponseEntity.ok(workerService.toggleWorkerActive(id));
    }

    @PatchMapping("/{id}/verify")
    @Operation(summary = "توثيق حساب عامل")
    public ResponseEntity<com.bena.api.common.dto.ApiResponse<WorkerDTO>> verifyWorker(@PathVariable Long id) {
        WorkerDTO worker = workerService.verifyWorker(id);
        return ResponseEntity.ok(com.bena.api.common.dto.ApiResponse.success(worker, "تم توثيق الحساب بنجاح"));
    }

    @PatchMapping("/{id}/toggle-featured")
    @Operation(summary = "تمييز/إلغاء تمييز عامل")
    public ResponseEntity<WorkerDTO> toggleFeatured(@PathVariable Long id) {
        return ResponseEntity.ok(workerService.toggleWorkerFeatured(id));
    }

    @PostMapping("/{id}/media")
    @Operation(summary = "إضافة صورة/فيديو لمعرض العامل")
    public ResponseEntity<WorkerMediaDTO> addMedia(
            @PathVariable Long id,
            @RequestParam MultipartFile file,
            @RequestParam(required = false) String caption
    ) throws IOException {
        return ResponseEntity.ok(workerService.addMedia(id, file, caption));
    }

    @PostMapping("/{id}/media/external")
    @Operation(summary = "إضافة رابط فيديو خارجي")
    public ResponseEntity<WorkerMediaDTO> addExternalVideo(
            @PathVariable Long id,
            @RequestParam String url,
            @RequestParam(required = false) String caption
    ) {
        return ResponseEntity.ok(workerService.addExternalVideo(id, url, caption));
    }

    @DeleteMapping("/media/{mediaId}")
    @Operation(summary = "حذف وسائط")
    public ResponseEntity<Void> deleteMedia(@PathVariable Long mediaId) {
        workerService.deleteMedia(mediaId);
        return ResponseEntity.ok().build();
    }
}
