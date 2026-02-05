package com.bena.api.module.ads.controller;

import com.bena.api.common.dto.ApiResponse;
import com.bena.api.module.ads.dto.AdvertisementResponse;
import com.bena.api.module.ads.enums.AdSection;
import com.bena.api.module.ads.mapper.AdvertisementMapper;
import com.bena.api.module.ads.service.AdvertisementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/v1/ads")
@RequiredArgsConstructor
@Tag(name = "Ads", description = "الإعلانات")
public class PublicAdvertisementController {

    private final AdvertisementService advertisementService;
    private final AdvertisementMapper mapper = new AdvertisementMapper();

    @GetMapping
    @Operation(summary = "الإعلانات النشطة حسب القسم", description = "يرجع فقط الإعلانات النشطة ضمن وقت العرض وبحسب القسم")
    public ResponseEntity<ApiResponse<List<AdvertisementResponse>>> getActiveAdsBySection(@RequestParam AdSection section) {
        List<AdvertisementResponse> ads = advertisementService
                .getActiveAdsBySection(section, OffsetDateTime.now())
                .stream()
                .map(mapper::toPublicResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(ads));
    }
}
