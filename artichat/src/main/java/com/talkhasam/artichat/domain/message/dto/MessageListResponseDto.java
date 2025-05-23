package com.talkhasam.artichat.domain.message.dto;

import java.util.List;

public record MessageListResponseDto(
        List<MessageDto> messageList,
        Long lastKey
) {
}
