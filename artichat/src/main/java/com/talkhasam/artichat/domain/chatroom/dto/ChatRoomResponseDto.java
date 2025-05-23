package com.talkhasam.artichat.domain.chatroom.dto;

import com.talkhasam.artichat.domain.chatroom.entity.ChatRoom;

import java.time.Instant;

public record ChatRoomResponseDto(
        Long id,
        String chatRoomName,
        String profileImg,
        Instant createdAt,
        Instant modifiedAt
) {
    public static ChatRoomResponseDto from(ChatRoom chatRoom) {
        return new ChatRoomResponseDto(
                chatRoom.getId(),
                chatRoom.getChatRoomName(),
                chatRoom.getProfileImg(),
                chatRoom.getCreatedAt(),
                chatRoom.getModifiedAt()
        );
    }
}