package com.bena.api.module.ebook.entity;

import com.bena.api.module.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ebook_purchases", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "ebook_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EbookPurchase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ebook_id", nullable = false)
    private Ebook ebook;

    @Column(name = "amount_paid", nullable = false, precision = 10, scale = 2)
    private BigDecimal amountPaid;

    @Column(length = 10)
    @Builder.Default
    private String currency = "IQD";

    @CreationTimestamp
    @Column(name = "purchased_at")
    private LocalDateTime purchasedAt;

    @Column(name = "last_opened_at")
    private LocalDateTime lastOpenedAt;

    @Column(name = "last_page")
    @Builder.Default
    private Integer lastPage = 1;
}
