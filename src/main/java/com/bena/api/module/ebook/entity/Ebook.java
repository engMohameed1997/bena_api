package com.bena.api.module.ebook.entity;

import com.bena.api.module.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ebooks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ebook {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id", nullable = false)
    private User publisher;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "cover_url", length = 500)
    private String coverUrl;

    @Column(name = "cover_data")
    private byte[] coverData;

    @Column(name = "cover_type", length = 50)
    private String coverType;

    @Column(name = "pdf_path", nullable = false, length = 500)
    private String pdfPath;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal price = BigDecimal.ZERO;

    @Column(length = 10)
    @Builder.Default
    private String currency = "IQD";

    @Column(name = "is_published")
    @Builder.Default
    private Boolean isPublished = true;

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(name = "total_purchases")
    @Builder.Default
    private Integer totalPurchases = 0;

    @Column(name = "publish_date")
    private LocalDateTime publishDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // التصنيفات المتاحة
    public enum Category {
        ENGINEERING("هندسة"),
        DESIGN("تصميم"),
        ARCHITECTURE("عمارة"),
        CONSTRUCTION("بناء"),
        REAL_ESTATE("عقارات"),
        OTHER("أخرى");

        private final String arabicName;

        Category(String arabicName) {
            this.arabicName = arabicName;
        }

        public String getArabicName() {
            return arabicName;
        }
    }
}
