package com.talkhasam.artichat.domain.chatuser.dto;

import com.talkhasam.artichat.domain.chatuser.entity.ChatUser;

import java.time.Instant;

public record ChatUserDto(
        String chatRoomId,
        String chatUserId,
        String nickname,
        Instant createdAt
) {
    public static ChatUserDto fromEntity(ChatUser u) {
        return new ChatUserDto(
                u.getChatRoomId(),
                u.getChatUserId(),
                u.getNickname(),
                u.getCreatedAt()
        );
    }
}