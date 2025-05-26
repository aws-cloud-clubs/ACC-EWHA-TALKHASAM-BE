package com.talkhasam.artichat.domain.chatroom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomPostResponseDto {
    private Long chatRoomId;

    public static ChatRoomResponseDto from(Long chatRoomId) {
        return ChatRoomResponseDto.builder()
                .chatRoomId(chatRoomId)
                .build();
    }
}
