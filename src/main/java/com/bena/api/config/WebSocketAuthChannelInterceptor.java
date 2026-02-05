package com.bena.api.config;

import com.bena.api.module.auth.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = getFirstNativeHeader(accessor, "Authorization");
            if (authHeader == null) {
                authHeader = getFirstNativeHeader(accessor, "authorization");
            }

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new IllegalArgumentException("يرجى تسجيل الدخول أولاً");
            }

            String token = authHeader.substring(7);
            String userId = jwtService.extractUserId(token);

            if (userId == null || userId.isBlank()) {
                throw new IllegalArgumentException("يرجى تسجيل الدخول أولاً");
            }

            accessor.setUser(new StompPrincipal(userId));
            if (accessor.getSessionAttributes() != null) {
                accessor.getSessionAttributes().put("userId", userId);
            }
            log.debug("WebSocket CONNECT authenticated userId={}", userId);
        }

        return message;
    }

    private String getFirstNativeHeader(StompHeaderAccessor accessor, String name) {
        List<String> values = accessor.getNativeHeader(name);
        if (values == null || values.isEmpty()) return null;
        return values.get(0);
    }
}
