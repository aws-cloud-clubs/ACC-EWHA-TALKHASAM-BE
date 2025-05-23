package com.talkhasam.artichat.domain.message.repository;

import com.talkhasam.artichat.domain.message.entity.Message;

public interface MessageRepository {
    void save(Message message);
    MessageDynamoRepository.PageResult<Message> findByChatRoomId(long chatRoomId, int limit, Long exclusiveStartMessageId);
}