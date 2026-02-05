package com.bena.api.module.portfolio.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "portfolio_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_item_id", nullable = false)
    private PortfolioItem portfolioItem;

    @Column(name = "image_url", columnDefinition = "TEXT", nullable = false)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "image_type", length = 20)
    @Builder.Default
    private ImageType imageType = ImageType.IMAGE;

    @Column(length = 255)
    private String caption;

    @Column(name = "is_cover")
    @Builder.Default
    private Boolean isCover = false;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum ImageType {
        IMAGE,
        VIDEO,
        BEFORE_AFTER
    }
}
