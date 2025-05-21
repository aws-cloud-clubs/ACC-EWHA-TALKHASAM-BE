package com.talkhasam.artichat.domain.message.controller;

import com.talkhasam.artichat.domain.message.dto.MessageRequestDto;
import com.talkhasam.artichat.domain.message.websocket.MessageSendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MessageSendController {

    private final MessageSendService chatService;

    @MessageMapping("/message.send")    // /app/message.send
    public void onMessage(@Valid MessageRequestDto msg) {
        log.info("[MessageSendController] 수신 → chatRoomId={}, chatUserId={}, content='{}'",
                msg.chatRoomId(), msg.chatUserId(), msg.content());
        try {
            chatService.sendToRoom(msg);
            log.info("[MessageSendController] 전송 성공 → chatRoomId={}, chatUserId={}",
                    msg.chatRoomId(), msg.chatUserId());
        } catch (Exception ex) {
            log.error("[MessageSendController] 전송 실패 → chatRoomId={}, chatUserId={}, error={}",
                    msg.chatRoomId(), msg.chatUserId(), ex.getMessage(), ex);
            // 필요에 따라 예외 재던지기 or 에러 프레임 전송
        }
    }
}