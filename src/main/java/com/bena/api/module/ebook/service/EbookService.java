package com.bena.api.module.ebook.service;

import com.bena.api.module.ebook.dto.*;
import com.bena.api.module.ebook.entity.*;
import com.bena.api.module.ebook.repository.*;
import com.bena.api.module.user.entity.User;
import com.bena.api.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class EbookService {

    private final EbookRepository ebookRepository;
    private final EbookPurchaseRepository purchaseRepository;
    private final EbookNoteRepository noteRepository;
    private final EbookReaderSettingsRepository settingsRepository;
    private final UserRepository userRepository;

    @Value("${app.ebook.upload-dir:uploads/ebooks}")
    private String uploadDir;

    @Value("${app.ebook.cover-dir:uploads/ebook-covers}")
    private String coverDir;

    // ==================== الكتب ====================

    public Page<EbookDto> getAllEbooks(EbookFilterRequest filter, UUID currentUserId) {
        Sort sort = getSort(filter.getSortBy(), filter.getSortDirection());
        Pageable pageable = PageRequest.of(
            filter.getPage() != null ? filter.getPage() : 0,
            filter.getSize() != null ? filter.getSize() : 20,
            sort
        );

        Page<Ebook> ebooks = ebookRepository.findWithFilters(
            filter.getCategory(),
            filter.getMinPrice(),
            filter.getMaxPrice(),
            filter.getSearch(),
            pageable
        );

        return ebooks.map(ebook -> toDto(ebook, currentUserId));
    }

    public List<EbookDto> getFeaturedEbooks(UUID currentUserId) {
        return ebookRepository.findByIsFeaturedTrueAndIsPublishedTrue()
            .stream()
            .map(ebook -> toDto(ebook, currentUserId))
            .toList();
    }

    public EbookDto getEbookById(UUID id, UUID currentUserId) {
        Ebook ebook = ebookRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("الكتاب غير موجود"));
        return toDto(ebook, currentUserId);
    }

    public List<String> getAllCategories() {
        return ebookRepository.findAllCategories();
    }

    // ==================== للناشر ====================

    @Transactional
    public EbookDto createEbook(EbookCreateRequest request, MultipartFile pdfFile, 
                                 MultipartFile coverFile, UUID publisherId) throws IOException {
        User publisher = userRepository.findById(publisherId)
            .orElseThrow(() -> new RuntimeException("المستخدم غير موجود"));

        // حفظ ملف PDF (نحتاج المسار الكامل للقراءة)
        String pdfPath = savePdfFile(pdfFile, uploadDir, publisherId.toString());
        
        // حفظ صورة الغلاف في Database كـ byte[]
        byte[] coverData = null;
        String coverType = null;
        if (coverFile != null && !coverFile.isEmpty()) {
            coverData = coverFile.getBytes();
            coverType = coverFile.getContentType();
        }

        Ebook ebook = Ebook.builder()
            .publisher(publisher)
            .title(request.getTitle())
            .description(request.getDescription())
            .category(request.getCategory())
            .price(request.getPrice())
            .currency(request.getCurrency() != null ? request.getCurrency() : "IQD")
            .pdfPath(pdfPath)
            .coverData(coverData)
            .coverType(coverType)
            .publishDate(LocalDateTime.now())
            .build();

        ebook = ebookRepository.save(ebook);
        log.info("تم إنشاء كتاب جديد: {} بواسطة {}", ebook.getTitle(), publisherId);
        
        return toDto(ebook, publisherId);
    }

    @Transactional
    public EbookDto updateEbook(UUID id, EbookCreateRequest request, 
                                 MultipartFile coverFile, UUID publisherId) throws IOException {
        Ebook ebook = ebookRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("الكتاب غير موجود"));

        if (!ebook.getPublisher().getId().equals(publisherId)) {
            throw new RuntimeException("لا تملك صلاحية تعديل هذا الكتاب");
        }

        ebook.setTitle(request.getTitle());
        ebook.setDescription(request.getDescription());
        ebook.setCategory(request.getCategory());
        ebook.setPrice(request.getPrice());
        
        if (request.getCurrency() != null) {
            ebook.setCurrency(request.getCurrency());
        }

        if (coverFile != null && !coverFile.isEmpty()) {
            ebook.setCoverData(coverFile.getBytes());
            ebook.setCoverType(coverFile.getContentType());
            ebook.setCoverUrl(null); // مسح الـ URL القديم
        }

        ebook = ebookRepository.save(ebook);
        return toDto(ebook, publisherId);
    }

    @Transactional
    public void deleteEbook(UUID id, UUID publisherId) {
        Ebook ebook = ebookRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("الكتاب غير موجود"));

        if (!ebook.getPublisher().getId().equals(publisherId)) {
            throw new RuntimeException("لا تملك صلاحية حذف هذا الكتاب");
        }

        ebookRepository.delete(ebook);
        log.info("تم حذف الكتاب: {} بواسطة {}", ebook.getTitle(), publisherId);
    }

    public Page<EbookDto> getMyBooks(UUID publisherId, Pageable pageable) {
        return ebookRepository.findByPublisherId(publisherId, pageable)
            .map(ebook -> toDto(ebook, publisherId));
    }

    public PublisherStatsDto getPublisherStats(UUID publisherId) {
        long totalBooks = ebookRepository.countByPublisherId(publisherId);
        long totalSales = purchaseRepository.getTotalSalesByPublisher(publisherId);
        BigDecimal totalEarnings = purchaseRepository.getTotalEarningsByPublisher(publisherId);

        return PublisherStatsDto.builder()
            .totalBooks(totalBooks)
            .totalSales(totalSales)
            .totalEarnings(totalEarnings != null ? totalEarnings : BigDecimal.ZERO)
            .currency("IQD")
            .build();
    }

    // ==================== الشراء والقراءة ====================

    @Transactional
    public EbookPurchaseDto purchaseEbook(UUID ebookId, UUID userId) {
        if (purchaseRepository.existsByUserIdAndEbookId(userId, ebookId)) {
            throw new RuntimeException("لقد اشتريت هذا الكتاب مسبقاً");
        }

        Ebook ebook = ebookRepository.findById(ebookId)
            .orElseThrow(() -> new RuntimeException("الكتاب غير موجود"));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("المستخدم غير موجود"));

        EbookPurchase purchase = EbookPurchase.builder()
            .user(user)
            .ebook(ebook)
            .amountPaid(ebook.getPrice())
            .currency(ebook.getCurrency())
            .build();

        purchase = purchaseRepository.save(purchase);

        // تحديث عدد المشتريات
        ebook.setTotalPurchases(ebook.getTotalPurchases() + 1);
        ebookRepository.save(ebook);

        log.info("تم شراء الكتاب: {} بواسطة {}", ebook.getTitle(), userId);
        return toPurchaseDto(purchase);
    }

    public boolean hasPurchased(UUID userId, UUID ebookId) {
        return purchaseRepository.existsByUserIdAndEbookId(userId, ebookId);
    }

    public Resource getEbookPdf(UUID ebookId, UUID userId) throws IOException {
        Ebook ebook = ebookRepository.findById(ebookId)
            .orElseThrow(() -> new RuntimeException("الكتاب غير موجود"));

        // التحقق من الشراء أو الملكية
        boolean isOwner = ebook.getPublisher().getId().equals(userId);
        if (!isOwner && !hasPurchased(userId, ebookId)) {
            throw new RuntimeException("يجب شراء الكتاب أولاً");
        }

        // تحديث آخر فتح (للمشترين فقط)
        if (!isOwner) {
            purchaseRepository.findByUserIdAndEbookId(userId, ebookId)
                .ifPresent(purchase -> {
                    purchase.setLastOpenedAt(LocalDateTime.now());
                    purchaseRepository.save(purchase);
                });
        }

        Path filePath = Paths.get(ebook.getPdfPath());
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new RuntimeException("ملف الكتاب غير موجود");
        }

        return resource;
    }

    @Transactional
    public void updateReadingProgress(UUID ebookId, UUID userId, Integer page) {
        EbookPurchase purchase = purchaseRepository.findByUserIdAndEbookId(userId, ebookId)
            .orElseThrow(() -> new RuntimeException("لم تشترِ هذا الكتاب"));
        
        purchase.setLastPage(page);
        purchase.setLastOpenedAt(LocalDateTime.now());
        purchaseRepository.save(purchase);
    }

    public Page<EbookPurchaseDto> getMyPurchases(UUID userId, Pageable pageable) {
        return purchaseRepository.findByUserId(userId, pageable)
            .map(this::toPurchaseDto);
    }

    // ==================== الملاحظات ====================

    public List<EbookNoteDto> getNotesForEbook(UUID ebookId, UUID userId) {
        return noteRepository.findByUserIdAndEbookIdOrderByPageNumberAsc(userId, ebookId)
            .stream()
            .map(this::toNoteDto)
            .toList();
    }

    @Transactional
    public EbookNoteDto addNote(UUID ebookId, UUID userId, EbookNoteRequest request) {
        if (!hasPurchased(userId, ebookId)) {
            throw new RuntimeException("يجب شراء الكتاب أولاً");
        }

        Ebook ebook = ebookRepository.findById(ebookId)
            .orElseThrow(() -> new RuntimeException("الكتاب غير موجود"));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("المستخدم غير موجود"));

        EbookNote note = EbookNote.builder()
            .user(user)
            .ebook(ebook)
            .pageNumber(request.getPageNumber())
            .noteText(request.getNoteText())
            .build();

        note = noteRepository.save(note);
        return toNoteDto(note);
    }

    @Transactional
    public void deleteNote(UUID noteId, UUID userId) {
        EbookNote note = noteRepository.findById(noteId)
            .orElseThrow(() -> new RuntimeException("الملاحظة غير موجودة"));

        if (!note.getUser().getId().equals(userId)) {
            throw new RuntimeException("لا تملك صلاحية حذف هذه الملاحظة");
        }

        noteRepository.delete(note);
    }

    // ==================== إعدادات القارئ ====================

    public ReaderSettingsDto getReaderSettings(UUID userId) {
        return settingsRepository.findByUserId(userId)
            .map(settings -> ReaderSettingsDto.builder()
                .fontSize(settings.getFontSize())
                .isDarkMode(settings.getIsDarkMode())
                .build())
            .orElse(ReaderSettingsDto.builder()
                .fontSize(16)
                .isDarkMode(false)
                .build());
    }

    @Transactional
    public ReaderSettingsDto updateReaderSettings(UUID userId, ReaderSettingsDto dto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("المستخدم غير موجود"));

        EbookReaderSettings settings = settingsRepository.findByUserId(userId)
            .orElse(EbookReaderSettings.builder().user(user).build());

        if (dto.getFontSize() != null) {
            settings.setFontSize(dto.getFontSize());
        }
        if (dto.getIsDarkMode() != null) {
            settings.setIsDarkMode(dto.getIsDarkMode());
        }

        settings = settingsRepository.save(settings);

        return ReaderSettingsDto.builder()
            .fontSize(settings.getFontSize())
            .isDarkMode(settings.getIsDarkMode())
            .build();
    }

    // ==================== Helper Methods ====================

    /**
     * حفظ ملف PDF - يُرجع المسار الكامل للقراءة
     */
    private String savePdfFile(MultipartFile file, String directory, String prefix) throws IOException {
        Path uploadPath = Paths.get(directory);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String filename = prefix + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.toString(); // مسار كامل للـ PDF
    }

    /**
     * حفظ صورة - يُرجع URL نسبي للعرض عبر HTTP
     */
    private String saveImageFile(MultipartFile file, String directory, String prefix) throws IOException {
        Path uploadPath = Paths.get(directory);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String filename = prefix + "_" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // إرجاع URL نسبي للصور
        return "/" + directory + "/" + filename;
    }

    private Sort getSort(String sortBy, String direction) {
        Sort.Direction dir = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        
        return switch (sortBy != null ? sortBy.toLowerCase() : "date") {
            case "price" -> Sort.by(dir, "price");
            case "purchases" -> Sort.by(dir, "totalPurchases");
            default -> Sort.by(dir, "publishDate");
        };
    }

    private EbookDto toDto(Ebook ebook, UUID currentUserId) {
        boolean isOwner = currentUserId != null && 
            ebook.getPublisher().getId().equals(currentUserId);
        
        boolean isPurchased = isOwner || (currentUserId != null && 
            purchaseRepository.existsByUserIdAndEbookId(currentUserId, ebook.getId()));

        // تحويل الصورة لـ Base64 data URL (مثل التصاميم)
        String coverUrl = ebook.getCoverUrl();
        if (coverUrl == null && ebook.getCoverData() != null && ebook.getCoverData().length > 0) {
            String contentType = ebook.getCoverType() != null ? ebook.getCoverType() : "image/jpeg";
            coverUrl = "data:" + contentType + ";base64," + java.util.Base64.getEncoder().encodeToString(ebook.getCoverData());
        }

        return EbookDto.builder()
            .id(ebook.getId())
            .title(ebook.getTitle())
            .description(ebook.getDescription())
            .coverUrl(coverUrl)
            .category(ebook.getCategory())
            .price(ebook.getPrice())
            .currency(ebook.getCurrency())
            .isFeatured(ebook.getIsFeatured())
            .totalPurchases(ebook.getTotalPurchases())
            .publishDate(ebook.getPublishDate())
            .publisherId(ebook.getPublisher().getId())
            .publisherName(ebook.getPublisher().getFullName())
            .publisherAvatar(ebook.getPublisher().getProfilePictureUrl())
            .isPurchased(isPurchased)
            .isOwner(isOwner)
            .build();
    }

    private EbookPurchaseDto toPurchaseDto(EbookPurchase purchase) {
        return EbookPurchaseDto.builder()
            .id(purchase.getId())
            .ebookId(purchase.getEbook().getId())
            .ebookTitle(purchase.getEbook().getTitle())
            .ebookCoverUrl(purchase.getEbook().getCoverUrl())
            .amountPaid(purchase.getAmountPaid())
            .currency(purchase.getCurrency())
            .purchasedAt(purchase.getPurchasedAt())
            .lastOpenedAt(purchase.getLastOpenedAt())
            .lastPage(purchase.getLastPage())
            .build();
    }

    private EbookNoteDto toNoteDto(EbookNote note) {
        return EbookNoteDto.builder()
            .id(note.getId())
            .pageNumber(note.getPageNumber())
            .noteText(note.getNoteText())
            .createdAt(note.getCreatedAt())
            .updatedAt(note.getUpdatedAt())
            .build();
    }
}
