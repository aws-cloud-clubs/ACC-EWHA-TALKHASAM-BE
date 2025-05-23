package com.talkhasam.artichat.domain.message.service;


import com.talkhasam.artichat.domain.message.dto.MessageDto;
import com.talkhasam.artichat.domain.message.dto.MessageListResponseDto;
import com.talkhasam.artichat.domain.message.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {

    private final MessageRepository messageRepository;

    /**
     * 채팅방의 메시지를 페이지 단위로 조회
     * @param chatRoomId  조회할 채팅방 ID
     * @param limit       페이지 크기
     * @param exclusiveStartKey  이전 페이지의 평가 키
     * @return MessageListResponseDto 페이징된 메시지와 다음 평가키
     */
    public MessageListResponseDto getMessageList(
            long chatRoomId,
            int limit,
            Map<String, AttributeValue> exclusiveStartKey
    ) {
        log.info("Fetching messages for chatRoomId={}, limit={}, startKey={}", chatRoomId, limit, exclusiveStartKey);

        // DynamoDB에서 페이징 조회
        var page = messageRepository.findByChatRoomId(chatRoomId, limit, exclusiveStartKey);

        // 엔티티를 DTO로 변환
        List<MessageDto> messages = page.getItems().stream()
                .map(m -> new MessageDto(
                        m.getId(),
                        m.getChatUserId(),
                        m.getNickname(),
                        m.isOwner(),
                        m.getContent(),
                        m.getCreatedAt()
                ))
                .collect(Collectors.toList());

        log.info("Found {} messages for chatRoomId={}", messages.size(), chatRoomId);

        // 응답에 메시지 목록과 다음 평가키 포함
        return new MessageListResponseDto(messages, page.getNextKey());
    }
}
