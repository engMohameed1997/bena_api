package com.bena.api.config;

import com.bena.api.module.auth.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtHandshakeHandler extends DefaultHandshakeHandler {

    private final JwtService jwtService;

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader == null) {
            authHeader = request.getHeaders().getFirst("authorization");
        }

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String userId = jwtService.extractUserId(token);
                if (userId != null && !userId.isBlank()) {
                    log.debug("WebSocket handshake principal userId={}", userId);
                    return new StompPrincipal(userId);
                }
            } catch (Exception e) {
                log.debug("WebSocket handshake invalid token: {}", e.getMessage());
            }
        }

        return super.determineUser(request, wsHandler, attributes);
    }
}
