package com.bena.api.module.ebook.controller;

import com.bena.api.module.ebook.dto.*;
import com.bena.api.module.ebook.service.EbookService;
import com.bena.api.module.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/ebooks")
@RequiredArgsConstructor
public class EbookController {

    private final EbookService ebookService;

    // ==================== Public Endpoints ====================

    /**
     * جلب قائمة الكتب مع الفلترة
     */
    @GetMapping
    public ResponseEntity<Page<EbookDto>> getAllEbooks(
            @ModelAttribute EbookFilterRequest filter,
            @AuthenticationPrincipal User currentUser) {
        UUID userId = currentUser != null ? currentUser.getId() : null;
        return ResponseEntity.ok(ebookService.getAllEbooks(filter, userId));
    }

    /**
     * جلب الكتب المميزة
     */
    @GetMapping("/featured")
    public ResponseEntity<List<EbookDto>> getFeaturedEbooks(
            @AuthenticationPrincipal User currentUser) {
        UUID userId = currentUser != null ? currentUser.getId() : null;
        return ResponseEntity.ok(ebookService.getFeaturedEbooks(userId));
    }

    /**
     * جلب تفاصيل كتاب
     */
    @GetMapping("/{id}")
    public ResponseEntity<EbookDto> getEbookById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        UUID userId = currentUser != null ? currentUser.getId() : null;
        return ResponseEntity.ok(ebookService.getEbookById(id, userId));
    }

    /**
     * جلب قائمة التصنيفات
     */
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(ebookService.getAllCategories());
    }

    // ==================== User Endpoints ====================

    /**
     * شراء كتاب
     */
    @PostMapping("/{id}/purchase")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EbookPurchaseDto> purchaseEbook(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ebookService.purchaseEbook(id, currentUser.getId()));
    }

    /**
     * قراءة الكتاب (PDF)
     */
    @GetMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource> readEbook(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) throws IOException {
        Resource resource = ebookService.getEbookPdf(id, currentUser.getId());
        
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"ebook.pdf\"")
            // منع التخزين المؤقت
            .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
            .header(HttpHeaders.PRAGMA, "no-cache")
            .header(HttpHeaders.EXPIRES, "0")
            .body(resource);
    }

    /**
     * تحديث تقدم القراءة
     */
    @PutMapping("/{id}/progress")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> updateProgress(
            @PathVariable UUID id,
            @RequestParam Integer page,
            @AuthenticationPrincipal User currentUser) {
        ebookService.updateReadingProgress(id, currentUser.getId(), page);
        return ResponseEntity.ok().build();
    }

    /**
     * جلب مشترياتي
     */
    @GetMapping("/my-purchases")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<EbookPurchaseDto>> getMyPurchases(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal User currentUser) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "purchasedAt"));
        return ResponseEntity.ok(ebookService.getMyPurchases(currentUser.getId(), pageable));
    }

    // ==================== Notes Endpoints ====================

    /**
     * جلب ملاحظاتي على كتاب
     */
    @GetMapping("/{id}/notes")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<EbookNoteDto>> getNotes(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ebookService.getNotesForEbook(id, currentUser.getId()));
    }

    /**
     * إضافة ملاحظة
     */
    @PostMapping("/{id}/notes")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EbookNoteDto> addNote(
            @PathVariable UUID id,
            @Valid @RequestBody EbookNoteRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ebookService.addNote(id, currentUser.getId(), request));
    }

    /**
     * حذف ملاحظة
     */
    @DeleteMapping("/notes/{noteId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteNote(
            @PathVariable UUID noteId,
            @AuthenticationPrincipal User currentUser) {
        ebookService.deleteNote(noteId, currentUser.getId());
        return ResponseEntity.ok().build();
    }

    // ==================== Reader Settings ====================

    /**
     * جلب إعدادات القارئ
     */
    @GetMapping("/reader-settings")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReaderSettingsDto> getReaderSettings(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ebookService.getReaderSettings(currentUser.getId()));
    }

    /**
     * تحديث إعدادات القارئ
     */
    @PutMapping("/reader-settings")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReaderSettingsDto> updateReaderSettings(
            @RequestBody ReaderSettingsDto settings,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ebookService.updateReaderSettings(currentUser.getId(), settings));
    }

    // ==================== Publisher Endpoints ====================

    /**
     * جلب كتبي (للناشر)
     */
    @GetMapping("/my-books")
    @PreAuthorize("hasAnyRole('ENGINEER', 'DESIGNER', 'CONTRACTOR', 'ADMIN')")
    public ResponseEntity<Page<EbookDto>> getMyBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal User currentUser) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(ebookService.getMyBooks(currentUser.getId(), pageable));
    }

    /**
     * إضافة كتاب جديد
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ENGINEER', 'DESIGNER', 'CONTRACTOR', 'ADMIN')")
    public ResponseEntity<EbookDto> createEbook(
            @Valid @ModelAttribute EbookCreateRequest request,
            @RequestParam("pdf") MultipartFile pdfFile,
            @RequestParam(value = "cover", required = false) MultipartFile coverFile,
            @AuthenticationPrincipal User currentUser) throws IOException {
        return ResponseEntity.ok(ebookService.createEbook(request, pdfFile, coverFile, currentUser.getId()));
    }

    /**
     * تعديل كتاب
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ENGINEER', 'DESIGNER', 'CONTRACTOR', 'ADMIN')")
    public ResponseEntity<EbookDto> updateEbook(
            @PathVariable UUID id,
            @Valid @ModelAttribute EbookCreateRequest request,
            @RequestParam(value = "cover", required = false) MultipartFile coverFile,
            @AuthenticationPrincipal User currentUser) throws IOException {
        return ResponseEntity.ok(ebookService.updateEbook(id, request, coverFile, currentUser.getId()));
    }

    /**
     * حذف كتاب
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ENGINEER', 'DESIGNER', 'CONTRACTOR', 'ADMIN')")
    public ResponseEntity<Void> deleteEbook(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser) {
        ebookService.deleteEbook(id, currentUser.getId());
        return ResponseEntity.ok().build();
    }

    /**
     * إحصائيات الناشر
     */
    @GetMapping("/my-stats")
    @PreAuthorize("hasAnyRole('ENGINEER', 'DESIGNER', 'CONTRACTOR', 'ADMIN')")
    public ResponseEntity<PublisherStatsDto> getMyStats(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ebookService.getPublisherStats(currentUser.getId()));
    }
}
