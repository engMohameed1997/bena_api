package com.bena.api.module.offers.controller;

import com.bena.api.module.offers.dto.*;
import com.bena.api.module.offers.entity.OfferType;
import com.bena.api.module.offers.service.ContractorOfferService;
import com.bena.api.module.offers.service.OfferRequestService;
import com.bena.api.module.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
@Tag(name = "عروض المقاولات", description = "API لإدارة عروض المقاولين والمهندسين والمصممين")
public class ContractorOfferController {

    private final ContractorOfferService offerService;
    private final OfferRequestService requestService;

    // ==================== العروض ====================

    @GetMapping
    @Operation(summary = "جلب جميع العروض", description = "جلب قائمة العروض مع إمكانية الفلترة")
    public ResponseEntity<Page<OfferListResponse>> getOffers(
            @RequestParam(required = false) OfferType offerType,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) Boolean verifiedOnly,
            @RequestParam(required = false) Long providerId,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        OfferFilterRequest filter = OfferFilterRequest.builder()
                .offerType(offerType)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .city(city)
                .area(area)
                .verifiedOnly(verifiedOnly)
                .providerId(providerId)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(offerService.getOffers(filter, pageable));
    }

    @GetMapping("/featured")
    @Operation(summary = "العروض المميزة", description = "جلب العروض المميزة")
    public ResponseEntity<List<OfferListResponse>> getFeaturedOffers() {
        return ResponseEntity.ok(offerService.getFeaturedOffers());
    }

    @GetMapping("/search")
    @Operation(summary = "البحث في العروض", description = "البحث النصي في العروض")
    public ResponseEntity<Page<OfferListResponse>> searchOffers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(offerService.searchOffers(query, pageable));
    }

    @GetMapping("/types")
    @Operation(summary = "أنواع العروض", description = "جلب جميع أنواع العروض المتاحة")
    public ResponseEntity<List<Map<String, String>>> getOfferTypes() {
        List<Map<String, String>> types = java.util.Arrays.stream(OfferType.values())
                .map(type -> Map.of(
                        "value", type.name(),
                        "label", type.getArabicName()
                ))
                .toList();
        return ResponseEntity.ok(types);
    }

    @GetMapping("/{id}")
    @Operation(summary = "تفاصيل عرض", description = "جلب تفاصيل عرض محدد")
    public ResponseEntity<OfferResponse> getOfferById(@PathVariable UUID id) {
        return ResponseEntity.ok(offerService.getOfferById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CONTRACTOR', 'ENGINEER', 'DESIGNER')")
    @Operation(summary = "إنشاء عرض", description = "إنشاء عرض جديد (للمهنيين فقط)")
    public ResponseEntity<OfferResponse> createOffer(
            @Valid @RequestBody OfferCreateRequest request,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(offerService.createOffer(request, user.getId()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CONTRACTOR', 'ENGINEER', 'DESIGNER')")
    @Operation(summary = "تحديث عرض", description = "تحديث عرض موجود")
    public ResponseEntity<OfferResponse> updateOffer(
            @PathVariable UUID id,
            @Valid @RequestBody OfferUpdateRequest request,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(offerService.updateOffer(id, request, user.getId()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CONTRACTOR', 'ENGINEER', 'DESIGNER', 'ADMIN')")
    @Operation(summary = "حذف عرض", description = "حذف عرض")
    public ResponseEntity<Void> deleteOffer(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user
    ) {
        offerService.deleteOffer(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('CONTRACTOR', 'ENGINEER', 'DESIGNER')")
    @Operation(summary = "عروضي", description = "جلب عروض المستخدم الحالي")
    public ResponseEntity<List<OfferListResponse>> getMyOffers(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(offerService.getMyOffers(user.getId()));
    }

    @PostMapping("/{id}/toggle-status")
    @PreAuthorize("hasAnyRole('CONTRACTOR', 'ENGINEER', 'DESIGNER')")
    @Operation(summary = "تفعيل/إيقاف عرض", description = "تغيير حالة العرض")
    public ResponseEntity<Void> toggleOfferStatus(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user
    ) {
        offerService.toggleOfferStatus(id, user.getId());
        return ResponseEntity.ok().build();
    }

    // ==================== الطلبات ====================

    @PostMapping("/{id}/request")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "طلب عرض", description = "إرسال طلب على عرض معين")
    public ResponseEntity<OfferRequestResponse> requestOffer(
            @PathVariable UUID id,
            @RequestBody(required = false) OfferRequestCreateDto dto,
            @AuthenticationPrincipal User user
    ) {
        if (dto == null) {
            dto = new OfferRequestCreateDto();
        }
        dto.setOfferId(id);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(requestService.createRequest(dto, user.getId()));
    }

    @GetMapping("/requests/my")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "طلباتي", description = "جلب طلبات المستخدم الحالي")
    public ResponseEntity<Page<OfferRequestResponse>> getMyRequests(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(requestService.getMyRequests(user.getId(), pageable));
    }

    @GetMapping("/requests/incoming")
    @PreAuthorize("hasAnyRole('CONTRACTOR', 'ENGINEER', 'DESIGNER')")
    @Operation(summary = "الطلبات الواردة", description = "جلب الطلبات الواردة للمهني")
    public ResponseEntity<Page<OfferRequestResponse>> getIncomingRequests(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(requestService.getIncomingRequests(user.getId(), pageable));
    }

    @GetMapping("/requests/{requestId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "تفاصيل طلب", description = "جلب تفاصيل طلب معين")
    public ResponseEntity<OfferRequestResponse> getRequestById(
            @PathVariable UUID requestId,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(requestService.getRequestById(requestId, user.getId()));
    }

    @PutMapping("/requests/{requestId}")
    @PreAuthorize("hasAnyRole('CONTRACTOR', 'ENGINEER', 'DESIGNER')")
    @Operation(summary = "تحديث حالة طلب", description = "تحديث حالة طلب (للمهني)")
    public ResponseEntity<OfferRequestResponse> updateRequestStatus(
            @PathVariable UUID requestId,
            @Valid @RequestBody OfferRequestUpdateDto dto,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(requestService.updateRequestStatus(requestId, dto, user.getId()));
    }

    @DeleteMapping("/requests/{requestId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "إلغاء طلب", description = "إلغاء طلب (للمستخدم)")
    public ResponseEntity<Void> cancelRequest(
            @PathVariable UUID requestId,
            @AuthenticationPrincipal User user
    ) {
        requestService.cancelRequest(requestId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/requests/stats")
    @PreAuthorize("hasAnyRole('CONTRACTOR', 'ENGINEER', 'DESIGNER')")
    @Operation(summary = "إحصائيات الطلبات", description = "إحصائيات طلبات المهني")
    public ResponseEntity<OfferRequestService.OfferRequestStats> getRequestStats(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(requestService.getRequestStats(user.getId()));
    }
}
