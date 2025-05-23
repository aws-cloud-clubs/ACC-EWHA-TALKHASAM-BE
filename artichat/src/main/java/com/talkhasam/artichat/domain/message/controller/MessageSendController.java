package com.talkhasam.artichat.domain.message.controller;

import com.talkhasam.artichat.domain.message.dto.MessageRequestDto;
import com.talkhasam.artichat.domain.message.service.MessageSendService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MessageSendController {
    private final MessageSendService messageSendService;

    @MessageMapping("/chatrooms/{chatRoomId}/messages") // /app/chatrooms/123/messages
    public void onMessage(
            @DestinationVariable @Positive long chatRoomId,
            @Payload @Valid MessageRequestDto requestDto,
            Principal principal
    ) {
        long chatUserId = Long.parseLong(principal.getName());
        messageSendService.sendToChatRoom(
                chatRoomId,
                chatUserId,
                requestDto.nickname(),
                requestDto.isOwner(),
                requestDto.content()
        );
    }
}