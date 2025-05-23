package com.talkhasam.artichat.domain.message.dto;

import com.talkhasam.artichat.domain.message.entity.Message;

import java.time.Instant;

public record MessageDto(
        long id,
        long chatUserId,
        String nickname,
        boolean isOwner,
        String content,
        Instant createdAt
) {
    public static MessageDto from(Message m) {
        return new MessageDto(
                m.getId(),
                m.getChatUserId(),
                m.getNickname(),
                m.isOwner(),
                m.getContent(),
                m.getCreatedAt()
        );
    }
}
