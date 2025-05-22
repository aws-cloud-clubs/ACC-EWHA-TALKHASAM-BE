package com.talkhasam.artichat.domain.message.dto;

import com.talkhasam.artichat.domain.message.entity.Message;

import java.time.Instant;

// 서버에서 클라이언트로 나가는 메시지 응답
public record MessageResponseDto(
        long id,
        long chatRoomId,
        long chatUserId,
        String nickname,
        Boolean isOwner,
        String content,
        Instant createdAt
) {
    public static MessageResponseDto from(Message message) {
        return new MessageResponseDto(
                message.getId(),
                message.getChatRoomId(),
                message.getChatUserId(),
                message.getNickname(),
                message.isOwner(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}