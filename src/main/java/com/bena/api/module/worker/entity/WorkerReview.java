package com.bena.api.module.worker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * تقييمات العامل
 */
@Entity
@Table(name = "worker_reviews")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    // اسم المُقيّم
    @Column(name = "reviewer_name", nullable = false)
    private String reviewerName;

    // التقييم من 1 إلى 5
    @Column(nullable = false)
    private Integer rating;

    // التعليق
    @Column(columnDefinition = "TEXT")
    private String comment;

    // هل معتمد (للمراجعة من الأدمن)
    @Column(name = "is_approved")
    @Builder.Default
    private Boolean isApproved = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
