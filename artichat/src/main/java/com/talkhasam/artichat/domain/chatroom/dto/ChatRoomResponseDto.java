package com.talkhasam.artichat.domain.chatroom.dto;

import com.talkhasam.artichat.domain.chatroom.entity.ChatRoom;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomResponseDto {
    private String chatRoomId; // ✅ 변경
    private String chatRoomName;
    private String owner;
    private String profileImg;
    private Instant createdAt;
    private Instant modifiedAt;

    public static ChatRoomResponseDto from(ChatRoom chatRoom, String owner) {
        return ChatRoomResponseDto.builder()
                .chatRoomId(String.valueOf(chatRoom.getId()))
                .chatRoomName(chatRoom.getChatRoomName())
                .owner(owner)
                .profileImg(chatRoom.getProfileImg())
                .createdAt(chatRoom.getCreatedAt())
                .modifiedAt(chatRoom.getModifiedAt())
                .build();
    }
}