package com.talkhasam.artichat.global.security;

import com.talkhasam.artichat.global.exception.CustomException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeFailureException;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtHandshakeHandler extends DefaultHandshakeHandler {
    private final CustomTokenService customTokenService;

    @Override
    protected Principal determineUser(
            ServerHttpRequest request,
            WebSocketHandler wsHandler,
            Map<String, Object> attrs) {
        String bearer = Optional.ofNullable(request.getHeaders().getFirst("Authorization"))
                .orElseThrow(() -> new HandshakeFailureException("Missing Authorization header"));

        if (!bearer.startsWith("Bearer ")) {
            throw new HandshakeFailureException("Invalid Authorization header");
        }

        String jwt = bearer.substring(7).trim();
        if (jwt.isEmpty()) {
            throw new HandshakeFailureException("JWT token is empty");
        }

        String userId;
        try {
            userId = customTokenService.extractChatUserId(jwt);
        } catch (CustomException | JwtException | IllegalArgumentException ex) {
            // CustomException 포함 모든 파싱 오류를 HandshakeFailureException 으로 변환
            throw new HandshakeFailureException("JWT parsing failed: " + ex.getMessage(), ex);
        }

        return new UsernamePasswordAuthenticationToken(userId, null, List.of());
    }
}