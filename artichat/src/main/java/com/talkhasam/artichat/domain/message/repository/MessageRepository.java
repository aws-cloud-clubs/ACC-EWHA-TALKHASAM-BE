package com.talkhasam.artichat.domain.message.repository;

import com.talkhasam.artichat.domain.message.entity.Message;
import com.talkhasam.artichat.global.common.PageResult;

public interface MessageRepository {
    void save(Message message);
    PageResult<Message> findByChatRoomId(long chatRoomId, int limit, Long exclusiveStartMessageId);
    PageResult<Message> findByChatRoomIdAndIsOwnerTrue(long chatRoomId, int limit, Long exclusiveStartMessageId);
    PageResult<Message> findByChatUserId(long chatUserId, int limit, Long exclusiveStartMessageId);
}