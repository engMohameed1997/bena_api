package com.bena.api.module.portfolio.controller;

import com.bena.api.module.portfolio.entity.PortfolioItem;
import com.bena.api.module.portfolio.entity.PortfolioImage;
import com.bena.api.module.portfolio.entity.WorkerDocument;
import com.bena.api.module.portfolio.service.PortfolioService;
import com.bena.api.module.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @PostMapping("/items")
    public ResponseEntity<?> createPortfolioItem(
            @AuthenticationPrincipal User user,
            @RequestParam Long workerId,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String projectType,
            @RequestParam(required = false) String locationCity,
            @RequestParam(required = false) String locationArea,
            @RequestParam(required = false) String completionDate,
            @RequestParam(required = false) BigDecimal budget,
            @RequestParam(required = false) Integer durationDays) {
        try {
            LocalDate date = completionDate != null ? LocalDate.parse(completionDate) : null;
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "يرجى تسجيل الدخول أولاً"));
            }

            PortfolioItem item = portfolioService.createPortfolioItemOwned(user.getId(), workerId, title, description,
                    category, projectType, locationCity, locationArea, date, budget, durationDays);
            return ResponseEntity.ok(Map.of("success", true, "data", item, "message", "تم إضافة العمل بنجاح"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/items/{itemId}/images")
    public ResponseEntity<?> addImage(
            @PathVariable Long itemId,
            @AuthenticationPrincipal User user,
            @RequestParam String imageUrl,
            @RequestParam(required = false) String imageType,
            @RequestParam(required = false) String caption,
            @RequestParam(required = false, defaultValue = "false") boolean isCover) {
        try {
            PortfolioImage.ImageType type = imageType != null ?
                    PortfolioImage.ImageType.valueOf(imageType.toUpperCase()) : PortfolioImage.ImageType.IMAGE;
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "يرجى تسجيل الدخول أولاً"));
            }

            PortfolioImage image = portfolioService.addImageToPortfolioOwned(user.getId(), itemId, imageUrl, type, caption, isCover);
            return ResponseEntity.ok(Map.of("success", true, "data", image, "message", "تم إضافة الصورة بنجاح"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<?> updatePortfolioItem(
            @PathVariable Long itemId,
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String projectType,
            @RequestParam(required = false) String locationCity,
            @RequestParam(required = false) String locationArea,
            @RequestParam(required = false) String completionDate,
            @RequestParam(required = false) BigDecimal budget,
            @RequestParam(required = false) Integer durationDays
    ) {
        try {
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "يرجى تسجيل الدخول أولاً"));
            }
            LocalDate date = completionDate != null ? LocalDate.parse(completionDate) : null;

            PortfolioItem item = portfolioService.updatePortfolioItem(
                    user.getId(),
                    itemId,
                    title,
                    description,
                    category,
                    projectType,
                    locationCity,
                    locationArea,
                    date,
                    budget,
                    durationDays
            );
            return ResponseEntity.ok(Map.of("success", true, "data", item, "message", "تم تحديث العمل بنجاح"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<?> deactivatePortfolioItem(
            @PathVariable Long itemId,
            @AuthenticationPrincipal User user
    ) {
        try {
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "يرجى تسجيل الدخول أولاً"));
            }
            portfolioService.deactivatePortfolioItem(user.getId(), itemId);
            return ResponseEntity.ok(Map.of("success", true, "message", "تم حذف العمل بنجاح"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<?> deletePortfolioImage(
            @PathVariable Long imageId,
            @AuthenticationPrincipal User user
    ) {
        try {
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "يرجى تسجيل الدخول أولاً"));
            }
            portfolioService.deletePortfolioImage(user.getId(), imageId);
            return ResponseEntity.ok(Map.of("success", true, "message", "تم حذف الصورة بنجاح"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/items/{itemId}/cover/{imageId}")
    public ResponseEntity<?> setCoverImage(
            @PathVariable Long itemId,
            @PathVariable Long imageId,
            @AuthenticationPrincipal User user
    ) {
        try {
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("success", false, "message", "يرجى تسجيل الدخول أولاً"));
            }
            PortfolioImage image = portfolioService.setCoverImage(user.getId(), itemId, imageId);
            return ResponseEntity.ok(Map.of("success", true, "data", image, "message", "تم تعيين صورة الغلاف"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/worker/{workerId}")
    public ResponseEntity<?> getWorkerPortfolio(@PathVariable Long workerId, Pageable pageable) {
        try {
            Page<PortfolioItem> items = portfolioService.getWorkerPortfolio(workerId, pageable);
            return ResponseEntity.ok(Map.of("success", true, "data", items));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/featured")
    public ResponseEntity<?> getFeaturedPortfolios(Pageable pageable) {
        try {
            Page<PortfolioItem> items = portfolioService.getFeaturedPortfolios(pageable);
            return ResponseEntity.ok(Map.of("success", true, "data", items));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<?> getPortfolioItem(@PathVariable Long id) {
        try {
            PortfolioItem item = portfolioService.getPortfolioItem(id);
            return ResponseEntity.ok(Map.of("success", true, "data", item));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // ==================== Documents ====================

    @PostMapping("/documents")
    public ResponseEntity<?> uploadDocument(
            @RequestParam Long workerId,
            @RequestParam String documentType,
            @RequestParam String documentUrl,
            @RequestParam(required = false) String documentNumber,
            @RequestParam(required = false) String issueDate,
            @RequestParam(required = false) String expiryDate) {
        try {
            WorkerDocument.DocumentType type = WorkerDocument.DocumentType.valueOf(documentType.toUpperCase());
            LocalDate issue = issueDate != null ? LocalDate.parse(issueDate) : null;
            LocalDate expiry = expiryDate != null ? LocalDate.parse(expiryDate) : null;

            WorkerDocument document = portfolioService.uploadDocument(workerId, type, documentUrl, documentNumber, issue, expiry);
            return ResponseEntity.ok(Map.of("success", true, "data", document, "message", "تم رفع الوثيقة بنجاح"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/documents/worker/{workerId}")
    public ResponseEntity<?> getWorkerDocuments(@PathVariable Long workerId) {
        try {
            List<WorkerDocument> documents = portfolioService.getWorkerDocuments(workerId);
            return ResponseEntity.ok(Map.of("success", true, "data", documents));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/documents/pending")
    public ResponseEntity<?> getPendingDocuments(Pageable pageable) {
        try {
            Page<WorkerDocument> documents = portfolioService.getPendingDocuments(pageable);
            return ResponseEntity.ok(Map.of("success", true, "data", documents));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/documents/{documentId}/verify")
    public ResponseEntity<?> verifyDocument(
            @PathVariable Long documentId,
            @RequestParam UUID adminId,
            @RequestParam boolean approved,
            @RequestParam(required = false) String notes) {
        try {
            WorkerDocument document = portfolioService.verifyDocument(documentId, adminId, approved, notes);
            return ResponseEntity.ok(Map.of("success", true, "data", document,
                    "message", approved ? "تم الموافقة على الوثيقة" : "تم رفض الوثيقة"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/worker/{workerId}/verified")
    public ResponseEntity<?> isWorkerVerified(@PathVariable Long workerId) {
        try {
            boolean verified = portfolioService.isWorkerVerified(workerId);
            return ResponseEntity.ok(Map.of("success", true, "verified", verified));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
