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

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 99) // 스프링의 빈 순서를 지정하는 애노테이션으로, StompHandler 의 우선순위를 설정
public class StompHandler implements ChannelInterceptor {
    private final CustomTokenService customTokenService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        // 메세지의 STOMP 헤더를 쉽게 접근하기 위한 유틸리티 클래스

        // websocket 연결시 헤더의 jwt token 유효성 검증
        if(StompCommand.CONNECT == accessor.getCommand()){
            log.info("[stomphandler] extract header");
            String token = accessor.getFirstNativeHeader("Authorization");
            log.info("[stomphandler] token : {}",token);
            if(!token.isEmpty()){
                token = token.replace("Bearer ","").trim();
                String chatUserId = customTokenService.extractUsername(token);
                log.info("[stomphandler] chatUserId : {}", chatUserId);

                Objects.requireNonNull(accessor.getSessionAttributes()).put("chatUserId", chatUserId); // 기존 헤더에 nickname 정보를 추가해 저장한다.
            } else {
                throw new CustomException(ErrorCode.NOT_AUTHENTICATED);
            }

        }
        return message;
    }
}
