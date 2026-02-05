package com.bena.api.module.offers.entity;

import com.bena.api.module.worker.entity.Worker;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * كيان العرض الرئيسي
 */
@Entity
@Table(name = "contractor_offers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractorOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "offer_type", nullable = false, length = 50)
    private OfferType offerType;

    @Column(name = "base_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal basePrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "price_unit", length = 30)
    @Builder.Default
    private PriceUnit priceUnit = PriceUnit.PROJECT;

    @Column(name = "min_area")
    private Integer minArea;

    @Column(name = "max_area")
    private Integer maxArea;

    @Column(name = "execution_days")
    private Integer executionDays;

    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;

    @Column(name = "cover_image_data", columnDefinition = "TEXT")
    private String coverImageData;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(name = "view_count")
    @Builder.Default
    private Integer viewCount = 0;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String area;

    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private Set<OfferFeature> features = new HashSet<>();

    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private Set<OfferImage> images = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    // Helper methods
    public void addFeature(OfferFeature feature) {
        features.add(feature);
        feature.setOffer(this);
    }

    public void removeFeature(OfferFeature feature) {
        features.remove(feature);
        feature.setOffer(null);
    }

    public void addImage(OfferImage image) {
        images.add(image);
        image.setOffer(this);
    }

    public void removeImage(OfferImage image) {
        images.remove(image);
        image.setOffer(null);
    }

    public void incrementViewCount() {
        this.viewCount = (this.viewCount == null ? 0 : this.viewCount) + 1;
    }
}
