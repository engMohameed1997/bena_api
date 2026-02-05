package com.bena.api.module.ebook.entity;

import com.bena.api.module.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ebook_reader_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EbookReaderSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "font_size")
    @Builder.Default
    private Integer fontSize = 16;

    @Column(name = "is_dark_mode")
    @Builder.Default
    private Boolean isDarkMode = false;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
