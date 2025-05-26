package com.talkhasam.artichat.global.websocket;

import com.talkhasam.artichat.global.exception.CustomException;
import com.talkhasam.artichat.global.exception.ErrorCode;
import com.talkhasam.artichat.global.security.CustomTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 99) // 스프링의 빈 순서를 지정하는 애노테이션으로, StompHandler 의 우선순위를 설정
public class StompHandler implements ChannelInterceptor {
    private final CustomTokenService customTokenService;

    // websocket 연결시 헤더의 jwt token 유효성을 검증하고 chatUserId를 저장
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT == accessor.getCommand()) {
            String token = accessor.getFirstNativeHeader("Authorization");

            if (token != null && !token.isEmpty()) {
                token = token.replace("Bearer ", "").trim();
                String chatUserIdStr = customTokenService.extractUsername(token);
                Long chatUserId = Long.valueOf(chatUserIdStr);

                Map<String, Object> sessionAttrs = accessor.getSessionAttributes();
                if (sessionAttrs == null) {
                    throw new CustomException(ErrorCode.SESSION_NOT_INITIALIZED);
                }
                sessionAttrs.put("chatUserId", chatUserId);
                log.info("[StompHandler] chatUserId : {}", chatUserId);
            } else {
                throw new CustomException(ErrorCode.NOT_AUTHENTICATED);
            }

        }
        return message;
    }
}
