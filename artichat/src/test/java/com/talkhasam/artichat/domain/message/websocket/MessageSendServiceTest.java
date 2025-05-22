package com.talkhasam.artichat.domain.message.websocket;


import com.talkhasam.artichat.domain.chatuser.entity.ChatUser;
import com.talkhasam.artichat.domain.message.dto.MessageResponseDto;
import com.talkhasam.artichat.domain.message.entity.Message;
import com.talkhasam.artichat.domain.message.repository.MessageRepository;
import com.talkhasam.artichat.domain.message.service.MessageSendService;
import com.talkhasam.artichat.global.redis.RedisService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

    @Captor
    private ArgumentCaptor<Message> messageCaptor;

    @Test
    void sendToRoom_savesAndBroadcasts() {
        // Given
        long roomId = 100L;
        ChatUser user = ChatUser.builder()
                .id(42L)
                .chatRoomId(roomId)
                .nickname("tester")
                .password("ignored")
                .createdAt(Instant.now())
                .isOwner(true)
                .build();
        String content = "hello world";

//        // When
//        service.sendToChatRoom(roomId, user, content);

        // Then: DynamoDB 저장 검증
        verify(messageRepository, times(1)).save(messageCaptor.capture());
        Message saved = messageCaptor.getValue();
        assertThat(saved.getChatRoomId()).isEqualTo(roomId);
        assertThat(saved.getChatUserId()).isEqualTo(user.getId());
        assertThat(saved.getNickname()).isEqualTo(user.getNickname());
        assertThat(saved.isOwner()).isTrue();
        assertThat(saved.getContent()).isEqualTo(content);
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getTtlEpoch()).isGreaterThan(Instant.now().getEpochSecond());

        // Then: STOMP 브로커 전송 검증
        String expectedDestination = "/topic/chatroom/" + roomId;
        verify(template, times(1))
                .convertAndSend(eq(expectedDestination), eq(MessageResponseDto.from(saved)));

        // Then: Redis Pub/Sub 전송 검증
        verify(redisService, times(1))
                .publish(eq(expectedDestination), eq(MessageResponseDto.from(saved)));
    }
}