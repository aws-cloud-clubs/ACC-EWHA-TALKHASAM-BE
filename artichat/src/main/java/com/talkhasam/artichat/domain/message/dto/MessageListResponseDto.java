package com.talkhasam.artichat.domain.message.dto;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.List;
import java.util.Map;

public record MessageListResponseDto(
        List<MessageDto> messageList,
        Map<String, AttributeValue> lastKey
) {
}
