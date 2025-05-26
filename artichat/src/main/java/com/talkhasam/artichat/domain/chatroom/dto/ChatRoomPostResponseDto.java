package com.talkhasam.artichat.domain.chatroom.dto;

import com.talkhasam.artichat.domain.chatuser.dto.ChatUserLoginDataDto;

public record ChatRoomPostResponseDto (
        long chatRoomId,
        ChatUserLoginDataDto chatUserLoginDataDto
){}