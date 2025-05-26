package com.talkhasam.artichat.global.config;

import com.talkhasam.artichat.global.security.CustomAuthenticationProvider;
import com.talkhasam.artichat.global.security.CustomAuthenticationToken;
import com.talkhasam.artichat.global.security.JwtHandshakeHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final CustomAuthenticationProvider customAuthenticationProvider;
    private final JwtHandshakeHandler handshakeHandler;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                .setHandshakeHandler(handshakeHandler)
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                if (!StompCommand.CONNECT.equals(accessor.getCommand())) {
                    return message;  // CONNECT 외엔 패스
                }

                // ① 헤더에서 Bearer 토큰 추출
                String bearer = accessor.getFirstNativeHeader("Authorization");
                if (bearer == null || !bearer.startsWith("Bearer ")) {
                    throw new IllegalArgumentException("Missing or invalid Authorization header");
                }
                String jwt = bearer.substring(7);

                // ② CustomAuthenticationToken 생성 (토큰 문자열 포함)
                CustomAuthenticationToken authRequest = new CustomAuthenticationToken(jwt, null);

                // ③ Provider에서 검증 → Authentication 리턴
                Authentication auth = customAuthenticationProvider.authenticate(authRequest);

                // ④ STOMP 세션에 Principal 설정
                accessor.setUser(auth);
                return message;
            }
        });
    }

}