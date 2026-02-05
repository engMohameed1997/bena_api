package com.bena.api.module.chat.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class WebSocketPresenceService {

    private final Set<String> connectedUsers = ConcurrentHashMap.newKeySet();

    public void markConnected(String userId) {
        if (userId == null || userId.isBlank()) return;
        connectedUsers.add(userId);
        log.debug("WS presence connected userId={}", userId);
    }

    public void markDisconnected(String userId) {
        if (userId == null || userId.isBlank()) return;
        connectedUsers.remove(userId);
        log.debug("WS presence disconnected userId={}", userId);
    }

    public boolean isOnline(UUID userId) {
        if (userId == null) return false;
        return connectedUsers.contains(userId.toString());
    }
}
