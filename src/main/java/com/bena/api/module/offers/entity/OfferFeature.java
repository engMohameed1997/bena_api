package com.bena.api.module.offers.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * كيان مميزات العرض
 */
@Entity
@Table(name = "offer_features")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferFeature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id", nullable = false)
    private ContractorOffer offer;

    @Column(name = "feature_text", nullable = false, length = 500)
    private String featureText;

    @Column(name = "is_included")
    @Builder.Default
    private Boolean isIncluded = true;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;
}
