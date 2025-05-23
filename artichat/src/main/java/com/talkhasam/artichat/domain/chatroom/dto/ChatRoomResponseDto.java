package com.talkhasam.artichat.domain.chatroom.dto;

public record ChatRoomResponseDto(
        Long id,
        String chatRoomName,
        String profileImg,
        String linkId,
        Instant createdAt,
        Instant modifiedAt
) {
    public static ChatRoomResponseDto from(ChatRoom chatRoom) {
        return new ChatRoomResponseDto(
                chatRoom.getId(),
                chatRoom.getChatRoomName(),
                chatRoom.getProfileImg(),
                chatRoom.getLinkId(),
                chatRoom.getCreatedAt(),
                chatRoom.getModifiedAt()
        );
    }
}