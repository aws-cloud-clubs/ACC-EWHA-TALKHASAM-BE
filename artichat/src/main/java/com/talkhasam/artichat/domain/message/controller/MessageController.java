package com.talkhasam.artichat.domain.message.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final ObjectMapper objectMapper;

    @Operation(summary = "채팅방 메세지 내역 조회")
    @GetMapping("/chatrooms/{chatRoomId}/messages")
    public ResponseEntity<MessageListResponseDto> getMessageList(
            @PathVariable @Positive long chatRoomId,
            @RequestParam(defaultValue = "20") @Positive int limit,
            @RequestParam(required = false) String startKeyJson
    ) throws JsonProcessingException {

        // 1) startKeyJson이 있을 때만 Map<String,AttributeValue>로 변환
        Map<String, AttributeValue> exclusiveStartKey = null;
        if (startKeyJson != null && !startKeyJson.isBlank()) {
            // 클라이언트에서 startKeyJson을
            // objectMapper.writeValueAsString(page.getNextKey())
            // 형태로 받은 뒤 이곳에서 역직렬화
            exclusiveStartKey = objectMapper.readValue(
                    startKeyJson,
                    new TypeReference<Map<String, AttributeValue>>() {}
            );
            log.info("Parsing startKey: {}", exclusiveStartKey);
        }

        // 2) 서비스 호출
        MessageListResponseDto dto = messageService.getMessageList(
                chatRoomId,
                limit,
                exclusiveStartKey
        );
        log.info("Returning {} messages (nextKey={})", dto.messageList().size(), dto.lastKey());

        return ResponseEntity.ok(dto);
    }
}
