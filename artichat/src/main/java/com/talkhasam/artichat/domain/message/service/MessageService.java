package com.talkhasam.artichat.domain.message.service;

import com.talkhasam.artichat.domain.message.dto.MessageDto;
import com.talkhasam.artichat.domain.message.dto.MessageListResponseDto;
import com.talkhasam.artichat.domain.message.repository.MessageRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {
    private final MessageRepository messageRepository;
    public MessageListResponseDto getMessageList(long chatRoomId, int limit, @Nullable Long startId) {
        var page = messageRepository.findByChatRoomId(chatRoomId, limit, startId);
        List<MessageDto> messages = page.getItems().stream()
                .map(MessageDto::from)
                .collect(Collectors.toList());
        Long nextKey = page.getNextKey();
        return new MessageListResponseDto(messages, nextKey);
    }

}
