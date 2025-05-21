package com.talkhasam.artichat.domain.message.websocket;

import com.talkhasam.artichat.domain.message.dto.MessageRequestDto;
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

    // 채팅방 전체에 브로드캐스트
    public void sendToRoom(MessageRequestDto messageDto) {
        Message message = Message.builder()
                .chatRoomId(messageDto.chatRoomId())
                .id(nextLong()) // TSID 생성
                .chatUserId(messageDto.chatUserId())
                .nickname(messageDto.nickname())
                .isOwner(messageDto.isOwner())
                .content(messageDto.content())
                .createdAt(Instant.now()) // 시각 생성
                .ttlEpoch(Instant.now().plus(180, ChronoUnit.DAYS).getEpochSecond())
                .build();

        // DynamoDB에 저장
        messageRepository.save(message);

        String destination = "/topic/chatroom/" + messageDto.chatRoomId();
        MessageResponseDto responseDto = MessageResponseDto.from(message);

        // 1) 내장 STOMP Broker로 즉시 브로드캐스트
        template.convertAndSend(destination, responseDto);
        // (2) Redis Pub/Sub
        redisService.publish(destination, responseDto);
    }
}