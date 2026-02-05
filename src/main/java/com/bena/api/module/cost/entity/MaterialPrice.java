package com.bena.api.module.cost.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "material_prices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "labor_cost", precision = 15, scale = 2)
    private BigDecimal laborCost;

    @Column(length = 3)
    @Builder.Default
    private String currency = "IQD";

    @Column(length = 50)
    @Builder.Default
    private String region = "baghdad";

    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    @Column(length = 100)
    private String source;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
}
