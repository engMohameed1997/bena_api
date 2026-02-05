package com.bena.api.module.offers.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * كيان صور العرض
 */
@Entity
@Table(name = "offer_images")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id", nullable = false)
    private ContractorOffer offer;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "image_data", columnDefinition = "TEXT")
    private String imageData;

    @Column(length = 200)
    private String caption;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;
}
