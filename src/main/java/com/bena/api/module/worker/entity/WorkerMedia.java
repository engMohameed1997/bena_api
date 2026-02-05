package com.bena.api.module.worker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * وسائط أعمال العامل (صور/فيديوهات)
 */
@Entity
@Table(name = "worker_media")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    // نوع الوسائط: IMAGE أو VIDEO
    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false)
    private MediaType mediaType;

    // رابط الملف - URL (للملفات الجديدة)
    @Column(name = "media_url", length = 500)
    private String mediaUrl;

    // صورة مصغرة
    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;
    
    // الأعمدة القديمة - للتوافق مع البيانات الموجودة
    @Column(name = "media_data")
    private byte[] mediaData;

    @Column(name = "content_type", length = 50)
    private String contentType;

    // رابط خارجي (للفيديوهات مثل YouTube)
    @Column(name = "external_url", length = 500)
    private String externalUrl;

    // وصف الصورة/الفيديو
    @Column(length = 255)
    private String caption;

    // ترتيب العرض
    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum MediaType {
        IMAGE,
        VIDEO
    }
}
