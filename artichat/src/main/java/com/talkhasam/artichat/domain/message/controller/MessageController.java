package com.talkhasam.artichat.domain.message.controller;

import com.talkhasam.artichat.domain.message.dto.MessageListResponseDto;
import com.talkhasam.artichat.domain.message.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @Operation(summary = "채팅방 메세지 내역 조회")
    @GetMapping("/chatrooms/{chatRoomId}/messages")
    public ResponseEntity<MessageListResponseDto> getMessageList(
            @PathVariable @Positive long chatRoomId,
            @RequestParam(defaultValue = "20") @Positive int limit,
            @RequestParam(required = false) Long startId
    ) {
        var response = messageService.getMessageList(chatRoomId, limit, startId);
        return ResponseEntity.ok(response);
    }
}
