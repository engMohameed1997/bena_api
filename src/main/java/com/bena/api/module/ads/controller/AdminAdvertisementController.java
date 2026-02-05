package com.bena.api.module.ads.controller;

import com.bena.api.common.dto.ApiResponse;
import com.bena.api.module.ads.dto.AdminAdvertisementResponse;
import com.bena.api.module.ads.dto.CreateAdvertisementRequest;
import com.bena.api.module.ads.dto.UpdateAdvertisementRequest;
import com.bena.api.module.ads.entity.Advertisement;
import com.bena.api.module.ads.mapper.AdvertisementMapper;
import com.bena.api.module.ads.service.AdvertisementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/admin/ads")
@RequiredArgsConstructor
@Tag(name = "Ads Admin", description = "إدارة الإعلانات (للأدمن)")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAdvertisementController {

    private final AdvertisementService advertisementService;
    private final AdvertisementMapper mapper = new AdvertisementMapper();

    @GetMapping
    @Operation(summary = "قائمة الإعلانات", description = "الحصول على جميع الإعلانات (نشطة/غير نشطة) للأدمن")
    public ResponseEntity<ApiResponse<Page<AdminAdvertisementResponse>>> list(Pageable pageable) {
        Page<AdminAdvertisementResponse> page = advertisementService.getAllAds(pageable)
                .map(mapper::toAdminResponse);

        return ResponseEntity.ok(ApiResponse.success(page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "تفاصيل إعلان", description = "الحصول على تفاصيل إعلان محدد")
    public ResponseEntity<ApiResponse<AdminAdvertisementResponse>> getById(@PathVariable UUID id) {
        Advertisement ad = advertisementService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(mapper.toAdminResponse(ad)));
    }

    @PostMapping
    @Operation(summary = "إنشاء إعلان", description = "إنشاء إعلان جديد")
    public ResponseEntity<ApiResponse<AdminAdvertisementResponse>> create(@Valid @RequestBody CreateAdvertisementRequest request) {
        Advertisement ad = advertisementService.create(request);
        return ResponseEntity.ok(ApiResponse.success(mapper.toAdminResponse(ad), "تم إنشاء الإعلان بنجاح"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "تحديث إعلان", description = "تحديث إعلان موجود")
    public ResponseEntity<ApiResponse<AdminAdvertisementResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAdvertisementRequest request
    ) {
        Advertisement ad = advertisementService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(mapper.toAdminResponse(ad), "تم تحديث الإعلان بنجاح"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "حذف إعلان", description = "حذف إعلان")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        advertisementService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "تم حذف الإعلان بنجاح"));
    }

    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "رفع صورة إعلان", description = "رفع صورة لاستخدامها داخل الإعلان")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        AdvertisementService.UploadResult result = advertisementService.uploadImage(file);
        return ResponseEntity.ok(ApiResponse.success(
                Map.of("path", result.path(), "url", result.url()),
                "تم رفع الصورة بنجاح"
        ));
    }
}
