package com.bena.api.module.worker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * صور طلب العمل
 */
@Entity
@Table(name = "job_request_images")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobRequestImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_request_id", nullable = false)
    private JobRequest jobRequest;

    @Column(name = "image_data")
    private byte[] imageData;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
