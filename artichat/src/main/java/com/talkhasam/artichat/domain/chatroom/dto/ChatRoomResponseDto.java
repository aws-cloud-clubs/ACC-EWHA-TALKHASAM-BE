package com.talkhasam.artichat.domain.chatroom.dto;

import com.talkhasam.artichat.domain.chatroom.entity.ChatRoom;

import java.time.Instant;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class ChatRoomResponseDto {

    private Long chatRoomId;
    private String chatRoomName;
    private String owner;
    private String profileImg;
    private Instant createdAt;
    private Instant modifiedAt;
}