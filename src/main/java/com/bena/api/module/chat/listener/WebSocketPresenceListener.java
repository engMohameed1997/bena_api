package com.bena.api.module.chat.listener;

import com.bena.api.module.chat.service.WebSocketPresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketPresenceListener {

    private final WebSocketPresenceService presenceService;

    @EventListener
    public void onSessionConnected(SessionConnectedEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String userId = null;
        if (sha.getSessionAttributes() != null) {
            Object v = sha.getSessionAttributes().get("userId");
            if (v != null) userId = v.toString();
        }

        if (userId == null || userId.isBlank()) {
            if (sha.getUser() != null) {
                userId = sha.getUser().getName();
            }
        }

        if (userId != null && !userId.isBlank()) {
            presenceService.markConnected(userId);
        }
    }

    @EventListener
    public void onSessionDisconnected(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String userId = null;
        if (sha.getSessionAttributes() != null) {
            Object v = sha.getSessionAttributes().get("userId");
            if (v != null) userId = v.toString();
        }

        if (userId == null || userId.isBlank()) {
            if (sha.getUser() != null) {
                userId = sha.getUser().getName();
            }
        }

        if (userId != null && !userId.isBlank()) {
            presenceService.markDisconnected(userId);
        }
    }
}
