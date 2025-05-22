package com.talkhasam.artichat.domain.message.service;

import com.talkhasam.artichat.domain.message.dto.MessageResponseDto;
import com.talkhasam.artichat.domain.message.entity.Message;
import com.talkhasam.artichat.domain.message.repository.MessageRepository;
import com.talkhasam.artichat.global.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.talkhasam.artichat.global.util.TsidGenerator.nextLong;

@Service
@RequiredArgsConstructor
public class MessageSendService {
    private final SimpMessagingTemplate template;
    private final MessageRepository messageRepository;
    private final RedisService redisService;

    public void sendToChatRoom(
            long chatRoomId,
            long chatUserId,
            String nickname,
            boolean isOwner,
            String content
    ) {
        // 1) 메시지 엔티티 생성
        Message message = Message.builder()
                .chatRoomId(chatRoomId)
                .id(nextLong())
                .chatUserId(chatUserId)
                .nickname(nickname)
                .isOwner(isOwner)
                .content(content)
                .createdAt(Instant.now())
                .ttlEpoch(Instant.now().plus(180, ChronoUnit.DAYS).getEpochSecond())
                .build();

        // 2) DynamoDB에 저장
        messageRepository.save(message);

        // 3) STOMP & Redis 브로드캐스트
        MessageResponseDto responseDto = MessageResponseDto.from(message);
        String dest = "/topic/chatrooms/" + chatRoomId + "/messages";
        template.convertAndSend(dest, responseDto);
        redisService.publish(dest, responseDto);
    }
}