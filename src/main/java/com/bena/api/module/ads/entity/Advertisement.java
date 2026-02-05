package com.bena.api.module.ads.entity;

import com.bena.api.module.ads.enums.AdSection;
import com.bena.api.module.ads.enums.AdTargetType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "advertisements")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Advertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 20)
    private AdTargetType targetType;

    @Column(name = "target_value", nullable = false, length = 500)
    private String targetValue;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "advertisement_sections",
            joinColumns = @JoinColumn(name = "advertisement_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "section", nullable = false, length = 30)
    @Builder.Default
    private Set<AdSection> sections = new HashSet<>();

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(nullable = false)
    @Builder.Default
    private Integer priority = 0;

    @Column(name = "start_at")
    private OffsetDateTime startAt;

    @Column(name = "end_at")
    private OffsetDateTime endAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
