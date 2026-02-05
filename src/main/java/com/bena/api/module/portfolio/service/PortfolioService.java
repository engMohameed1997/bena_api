package com.bena.api.module.portfolio.service;

import com.bena.api.module.portfolio.entity.PortfolioItem;
import com.bena.api.module.portfolio.entity.PortfolioImage;
import com.bena.api.module.portfolio.entity.WorkerDocument;
import com.bena.api.module.portfolio.repository.PortfolioItemRepository;
import com.bena.api.module.portfolio.repository.PortfolioImageRepository;
import com.bena.api.module.portfolio.repository.WorkerDocumentRepository;
import com.bena.api.module.worker.entity.Worker;
import com.bena.api.module.worker.repository.WorkerRepository;
import com.bena.api.module.user.entity.User;
import com.bena.api.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioItemRepository portfolioItemRepository;
    private final PortfolioImageRepository portfolioImageRepository;
    private final WorkerDocumentRepository workerDocumentRepository;
    private final WorkerRepository workerRepository;
    private final UserRepository userRepository;

    private void assertWorkerOwner(UUID userId, Worker worker) {
        if (userId == null) {
            throw new RuntimeException("يرجى تسجيل الدخول أولاً");
        }
        if (worker == null || worker.getUserId() == null || !worker.getUserId().equals(userId)) {
            throw new RuntimeException("غير مصرح لك بهذا الإجراء");
        }
    }

    private PortfolioItem getOwnedPortfolioItem(UUID userId, Long itemId) {
        PortfolioItem item = portfolioItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("البيانات غير موجودة"));
        assertWorkerOwner(userId, item.getWorker());
        return item;
    }

    // ==================== Portfolio Items ====================

    @Transactional
    public PortfolioItem createPortfolioItem(Long workerId, String title, String description,
                                              String category, String projectType, String locationCity,
                                              String locationArea, LocalDate completionDate,
                                              BigDecimal budget, Integer durationDays) {
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("المختص غير موجود"));

        PortfolioItem item = PortfolioItem.builder()
                .worker(worker)
                .title(title)
                .description(description)
                .category(category)
                .projectType(projectType)
                .locationCity(locationCity)
                .locationArea(locationArea)
                .completionDate(completionDate)
                .budget(budget)
                .durationDays(durationDays)
                .isActive(true)
                .build();

        return portfolioItemRepository.save(item);
    }

    @Transactional
    public PortfolioItem createPortfolioItemOwned(UUID userId, Long workerId, String title, String description,
                                                  String category, String projectType, String locationCity,
                                                  String locationArea, LocalDate completionDate,
                                                  BigDecimal budget, Integer durationDays) {
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("المختص غير موجود"));
        assertWorkerOwner(userId, worker);
        return createPortfolioItem(workerId, title, description, category, projectType, locationCity, locationArea,
                completionDate, budget, durationDays);
    }

    @Transactional
    public PortfolioImage addImageToPortfolio(Long portfolioItemId, String imageUrl,
                                               PortfolioImage.ImageType imageType, String caption, boolean isCover) {
        PortfolioItem item = portfolioItemRepository.findById(portfolioItemId)
                .orElseThrow(() -> new RuntimeException("عنصر المعرض غير موجود"));

        if (Boolean.TRUE.equals(isCover)) {
            List<PortfolioImage> existing = portfolioImageRepository.findByPortfolioItemIdOrderByDisplayOrderAsc(portfolioItemId);
            for (PortfolioImage img : existing) {
                img.setIsCover(false);
            }
            portfolioImageRepository.saveAll(existing);
        }

        PortfolioImage image = PortfolioImage.builder()
                .portfolioItem(item)
                .imageUrl(imageUrl)
                .imageType(imageType)
                .caption(caption)
                .isCover(isCover)
                .displayOrder(item.getImages().size())
                .build();

        return portfolioImageRepository.save(image);
    }

    @Transactional
    public PortfolioImage addImageToPortfolioOwned(UUID userId, Long portfolioItemId, String imageUrl,
                                                   PortfolioImage.ImageType imageType, String caption, boolean isCover) {
        PortfolioItem item = getOwnedPortfolioItem(userId, portfolioItemId);
        return addImageToPortfolio(item.getId(), imageUrl, imageType, caption, isCover);
    }

    @Transactional
    public PortfolioItem updatePortfolioItem(UUID userId, Long itemId, String title, String description,
                                             String category, String projectType, String locationCity,
                                             String locationArea, LocalDate completionDate,
                                             BigDecimal budget, Integer durationDays) {
        PortfolioItem item = getOwnedPortfolioItem(userId, itemId);

        if (title != null && !title.isBlank()) item.setTitle(title);
        if (description != null) item.setDescription(description);
        if (category != null) item.setCategory(category);
        if (projectType != null) item.setProjectType(projectType);
        if (locationCity != null) item.setLocationCity(locationCity);
        if (locationArea != null) item.setLocationArea(locationArea);
        if (completionDate != null) item.setCompletionDate(completionDate);
        if (budget != null) item.setBudget(budget);
        if (durationDays != null) item.setDurationDays(durationDays);

        return portfolioItemRepository.save(item);
    }

    @Transactional
    public void deactivatePortfolioItem(UUID userId, Long itemId) {
        PortfolioItem item = getOwnedPortfolioItem(userId, itemId);
        item.setIsActive(false);
        portfolioItemRepository.save(item);
    }

    @Transactional
    public void deletePortfolioImage(UUID userId, Long imageId) {
        PortfolioImage image = portfolioImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("البيانات غير موجودة"));
        PortfolioItem item = image.getPortfolioItem();
        assertWorkerOwner(userId, item.getWorker());

        boolean wasCover = Boolean.TRUE.equals(image.getIsCover());
        Long itemId = item.getId();
        portfolioImageRepository.delete(image);

        List<PortfolioImage> images = portfolioImageRepository.findByPortfolioItemIdOrderByDisplayOrderAsc(itemId);

        int order = 0;
        for (PortfolioImage img : images) {
            img.setDisplayOrder(order++);
        }

        if (wasCover && !images.isEmpty()) {
            images.get(0).setIsCover(true);
        }

        portfolioImageRepository.saveAll(images);
    }

    @Transactional
    public PortfolioImage setCoverImage(UUID userId, Long itemId, Long imageId) {
        PortfolioItem item = getOwnedPortfolioItem(userId, itemId);
        PortfolioImage image = portfolioImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("البيانات غير موجودة"));
        if (image.getPortfolioItem() == null || !image.getPortfolioItem().getId().equals(item.getId())) {
            throw new RuntimeException("البيانات غير موجودة");
        }

        List<PortfolioImage> images = portfolioImageRepository.findByPortfolioItemIdOrderByDisplayOrderAsc(itemId);
        for (PortfolioImage img : images) {
            img.setIsCover(img.getId().equals(imageId));
        }
        portfolioImageRepository.saveAll(images);

        return portfolioImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("البيانات غير موجودة"));
    }

    public Page<PortfolioItem> getWorkerPortfolio(Long workerId, Pageable pageable) {
        return portfolioItemRepository.findByWorkerIdAndIsActiveOrderByCreatedAtDesc(workerId, true, pageable);
    }

    public Page<PortfolioItem> getFeaturedPortfolios(Pageable pageable) {
        return portfolioItemRepository.findByIsFeaturedAndIsActiveOrderByCreatedAtDesc(true, true, pageable);
    }

    public PortfolioItem getPortfolioItem(Long id) {
        PortfolioItem item = portfolioItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("عنصر المعرض غير موجود"));
        item.setViewCount(item.getViewCount() + 1);
        return portfolioItemRepository.save(item);
    }

    // ==================== Worker Documents ====================

    @Transactional
    public WorkerDocument uploadDocument(Long workerId, WorkerDocument.DocumentType documentType,
                                          String documentUrl, String documentNumber,
                                          LocalDate issueDate, LocalDate expiryDate) {
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("المختص غير موجود"));

        WorkerDocument document = WorkerDocument.builder()
                .worker(worker)
                .documentType(documentType)
                .documentUrl(documentUrl)
                .documentNumber(documentNumber)
                .issueDate(issueDate)
                .expiryDate(expiryDate)
                .verificationStatus(WorkerDocument.VerificationStatus.PENDING)
                .build();

        return workerDocumentRepository.save(document);
    }

    public List<WorkerDocument> getWorkerDocuments(Long workerId) {
        return workerDocumentRepository.findByWorkerIdOrderByCreatedAtDesc(workerId);
    }

    public Page<WorkerDocument> getPendingDocuments(Pageable pageable) {
        return workerDocumentRepository.findByVerificationStatusOrderByCreatedAtDesc(
                WorkerDocument.VerificationStatus.PENDING, pageable);
    }

    @Transactional
    public WorkerDocument verifyDocument(Long documentId, UUID adminId, boolean approved, String notes) {
        WorkerDocument document = workerDocumentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("الوثيقة غير موجودة"));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("المسؤول غير موجود"));

        document.setVerificationStatus(approved ?
                WorkerDocument.VerificationStatus.APPROVED : WorkerDocument.VerificationStatus.REJECTED);
        document.setVerificationNotes(notes);
        document.setVerifiedBy(admin);
        document.setVerifiedAt(LocalDateTime.now());

        // تحديث حالة التحقق في Worker
        if (approved) {
            Worker worker = document.getWorker();
            worker.setIsVerified(true);
            workerRepository.save(worker);
        }

        return workerDocumentRepository.save(document);
    }

    public boolean isWorkerVerified(Long workerId) {
        long approvedCount = workerDocumentRepository.countByWorkerIdAndVerificationStatus(
                workerId, WorkerDocument.VerificationStatus.APPROVED);
        return approvedCount > 0;
    }
}
