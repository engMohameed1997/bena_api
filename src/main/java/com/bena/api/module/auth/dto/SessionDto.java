package com.bena.api.module.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionDto {
    private Long id;
    private String userEmail;
    private String userName;
    private String userRole;
    private String deviceInfo;
    private String ipAddress;
    private OffsetDateTime lastUsedAt;
    private OffsetDateTime expiresAt;
    private boolean isCurrent;
    private boolean isRevoked;
}
