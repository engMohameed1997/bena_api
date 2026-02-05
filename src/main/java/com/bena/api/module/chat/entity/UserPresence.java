package com.bena.api.module.chat.entity;

import com.bena.api.module.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_presence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPresence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(name = "is_online")
    @Builder.Default
    private Boolean isOnline = false;

    @Column(name = "last_seen_at")
    @UpdateTimestamp
    private LocalDateTime lastSeenAt;

    @Column(name = "current_conversation_id")
    private Long currentConversationId;
}
