package com.talkhasam.artichat.domain.message.websocket;

import com.talkhasam.artichat.domain.message.dto.MessageResponseDto;
import com.talkhasam.artichat.domain.message.entity.Message;
import com.talkhasam.artichat.domain.message.repository.MessageRepository;
import com.talkhasam.artichat.domain.message.service.MessageSendService;
import com.talkhasam.artichat.global.redis.RedisService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageSendServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private SimpMessagingTemplate template;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private MessageSendService service;

    @Test
    void sendToRoom_savesAndBroadcasts() {
        // given
        long chatRoomId = 42L;
        long chatUserId = 99L;
        String nickname = "tester";
        boolean isOwner= true;
        String content = "Hello, world!";

        // when
        service.sendToChatRoom(chatRoomId, chatUserId, nickname, isOwner, content);

        // then: repository.save(...) 호출 검증
        ArgumentCaptor<Message> msgCaptor = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository, times(1)).save(msgCaptor.capture());
        Message saved = msgCaptor.getValue();

        assertEquals(chatRoomId,     saved.getChatRoomId());
        assertEquals(chatUserId,     saved.getChatUserId());
        assertEquals(nickname,       saved.getNickname());
        assertEquals(isOwner,    saved.isOwner());
        assertEquals(content,    saved.getContent());
        assertNotNull(saved.getCreatedAt());
        assertTrue(saved.getTtlEpoch() > Instant.now().getEpochSecond());

        // then: STOMP 브로드캐스트 검증
        String expectedDest = "/topic/chatrooms/" + chatRoomId + "/messages";
        ArgumentCaptor<MessageResponseDto> dtoCaptor = ArgumentCaptor.forClass(MessageResponseDto.class);
        verify(template, times(1)).convertAndSend(eq(expectedDest), dtoCaptor.capture());
        MessageResponseDto dto = dtoCaptor.getValue();

        // dto 필드 일치 검증
        assertEquals(saved.getId(),         dto.id());
        assertEquals(saved.getChatRoomId(), dto.chatRoomId());
        assertEquals(saved.getChatUserId(), dto.chatUserId());
        assertEquals(saved.getNickname(),   dto.nickname());
        assertEquals(saved.getContent(),    dto.content());
        assertEquals(saved.getCreatedAt(),  dto.createdAt());

        // then: Redis 퍼블리시 검증
        verify(redisService, times(1)).publish(expectedDest, dto);
    }
}
