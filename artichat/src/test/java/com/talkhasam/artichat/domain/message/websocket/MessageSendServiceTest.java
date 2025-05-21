package com.talkhasam.artichat.domain.message.websocket;

import com.talkhasam.artichat.domain.message.dto.MessageRequestDto;
import com.talkhasam.artichat.domain.message.dto.MessageResponseDto;
import com.talkhasam.artichat.domain.message.repository.MessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MessageSendServiceTest {

    @Mock
    private SimpMessagingTemplate template;
    @Mock private MessageRepository messageRepository;

    @InjectMocks
    private MessageSendService service;

    @Test
    void sendToRoom_savesAndBroadcasts() {
        // Given
        MessageRequestDto dto = new MessageRequestDto(1L, 42L, "nick", true, "hello");
        // When
        service.sendToRoom(dto);
        // Then
        // 1) DynamoDB에 저장 검증
        verify(messageRepository).save(argThat(msg ->
                msg.getChatRoomId()==1L
                        && msg.getChatUserId()==42L
                        && msg.getContent().equals("hello")
        ));
        // STOMP 브로커 전송 검증: 두 번째 인자를 MessageResponseDto 로 고정
        verify(template).convertAndSend(
                eq("/topic/chatroom/1"),
                any(MessageResponseDto.class)
        );
        ArgumentCaptor<MessageResponseDto> cap = ArgumentCaptor.forClass(MessageResponseDto.class);
        verify(template).convertAndSend(eq("/topic/chatroom/1"), cap.capture());
        MessageResponseDto sent = cap.getValue();
        assertEquals("hello", sent.content());
    }
}
