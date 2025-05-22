package com.talkhasam.artichat.domain.message.repository;

import com.talkhasam.artichat.domain.message.entity.Message;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

public interface MessageRepository {
    void save(Message message);
    MessageDynamoRepository.PageResult<Message> findByChatRoomId(
            long chatRoomId,
            int limit,
            Map<String, AttributeValue> exclusiveStartKey);
}